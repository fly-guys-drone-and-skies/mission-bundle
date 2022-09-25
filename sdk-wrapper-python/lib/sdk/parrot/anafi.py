from type.domain import GeoLocation3D, Orientation
from type.event import *
from sdk.drone import Drone

from olympe import Drone as OlympeDrone, log, VideoFrame
from olympe.arsdkng.messages import ArsdkMessage
from olympe.enums.ardrone3.Piloting import MoveTo_Orientation_mode
from olympe.messages import gimbal
from olympe.messages.ardrone3.GPSSettingsState import GPSFixStateChanged, HomeChanged
from olympe.messages.ardrone3.Piloting import Landing, moveTo, TakeOff
from olympe.messages.ardrone3.PilotingState import AttitudeChanged, FlyingStateChanged, moveToChanged, PositionChanged
from olympe.messages.camera import set_zoom_target, start_recording, stop_recording
from olympe.messages.common.CommonState import BatteryStateChanged

from ipaddress import IPv4Address
import logging
import math
from typing import Callable, Dict

# Tolerances for GPS latitude/longitude comparisons
# https://docs.python.org/3/library/math.html#math.isclose
# 5 decimal places in rel_tol gives approximately 3-4 feet of lat/lon precision
float_tol = (1e-5, 1e-9)  # (rel tol, abs tol)


# def set_logging_levels(level):
#     config = log.get_config(None)
#     for logger in config['loggers']:
#         config['loggers'][logger]['level'] = level
#     log.set_config(config)
#
#
# set_logging_levels("WARN")


class ParrotAnafiDrone(Drone):

    NAME = "parrot-anafi"
    TIMEOUT_SECONDS = 5 * 60  # default timeout of 5 minutes

    def __init__(self, ip_address: IPv4Address):
        super().__init__()

        self.drone = OlympeDrone(str(ip_address))
        self.in_air = False

        self.subscriptions = self.__subscribe({
            BatteryStateChanged(): lambda event, _: self.__handle_battery_update(event),
            PositionChanged(): lambda event, _: self.__handle_position_update(event),
            AttitudeChanged(): lambda event, _: self.__handle_attitude_update(event)
        })

        self.drone.connect()

    def __subscribe(self, events: Dict[ArsdkMessage, Callable]):
        return [self.drone.subscribe(callback, event) for event, callback in events.items()]

    def __handle_battery_update(self, event: BatteryStateChanged):
        self.event(
            BatteryStatusEvent(
                percentage_remaining=event.args['percent']
            )
        )

    def __handle_position_update(self, event: PositionChanged):
        self.event(
            PositionStatusEvent(
                GeoLocation3D(
                    latitude_deg=event.args['latitude'],
                    longitude_deg=event.args['longitude'],
                    altitude_relative_m=event.args['altitude']
                )
            )
        )

    def __handle_attitude_update(self, event: AttitudeChanged):
        self.event(
            OrientationStatusEvent(
                Orientation(
                    pitch_deg=math.degrees(event.args['pitch']),
                    yaw_deg=math.degrees(event.args['yaw']),
                    roll_deg=math.degrees(event.args['roll'])
                )
            )
        )

    def is_connected(self) -> bool:
        return self.drone.connection_state().OK

    def takeoff(self):
        if not self.drone(start_recording(cam_id=0)).wait().success():
            logging.warning("Could not start recording")

        self.drone(
            TakeOff()
            >> FlyingStateChanged(state="hovering", _timeout=30)
        ).wait()

        self.in_air = True

    def land(self):
        self.drone(
            FlyingStateChanged(state="hovering", _timeout=30) >>
            Landing()
        ).wait()

        self.in_air = False

        if not self.drone(stop_recording(cam_id=0)).wait().success():
            logging.warning("Could not stop recording")

    def move_to(self, location: GeoLocation3D, heading_deg: Optional[float] = None):
        """
        https://developer.parrot.com/docs/olympe/arsdkng_ardrone3_piloting.html#olympe.messages.ardrone3.Piloting.moveTo
        """

        if heading_deg is None:
            orientation_mode = MoveTo_Orientation_mode.TO_TARGET  # Heading is the target location
            heading_deg = 0.0  # Will be ignored since TO_TARGET is used
        else:
            orientation_mode = MoveTo_Orientation_mode.HEADING_START  # Heading is set before moving

        # NOTE: It's unclear if when we want to redirect the drone mid-flight (before this command completes),
        #   can we just send another moveTo or is CancelMoveTo() required?
        self.drone(
            moveTo(
                latitude=location.latitude_deg,
                longitude=location.longitude_deg,
                altitude=location.altitude_relative_m,  # Relative to takeoff altitude
                orientation_mode=orientation_mode,
                heading=heading_deg,
                _float_tol=float_tol
            ) >> moveToChanged(status="DONE", _float_tol=float_tol, _timeout=self.TIMEOUT_SECONDS)
        ).wait()

    def move_gimbal(self, orientation: Orientation):
        self.drone(gimbal.set_target(
            gimbal_id=0,
            control_mode="position",
            yaw_frame_of_reference="none" if orientation.yaw_deg == 0.0 else "absolute",
            yaw=orientation.yaw_deg,
            pitch_frame_of_reference="none" if orientation.pitch_deg == 0.0 else "absolute",
            pitch=orientation.pitch_deg,
            roll_frame_of_reference="none" if orientation.roll_deg == 0.0 else "absolute",
            roll=orientation.roll_deg,
        )).wait()

    def set_zoom_level(self, zoom_level: float):
        self.drone(set_zoom_target(
            cam_id=0,
            control_mode='level',
            target=zoom_level
        )).wait()

    def get_location(self) -> GeoLocation3D:
        # Can only proceed if GPS has a signal
        assert self.drone(GPSFixStateChanged(fixed=1, _timeout=self.TIMEOUT_SECONDS, _policy="check_wait")).wait().success()

        if self.in_air:
            position = self.drone.get_state(PositionChanged)
        else:
            position = self.drone.get_state(HomeChanged)

        return GeoLocation3D(
            latitude_deg=position['latitude'],
            longitude_deg=position['longitude'],
            altitude_relative_m=position['altitude']
        )

    def get_orientation(self) -> Orientation:
        orientation = self.drone.get_state(AttitudeChanged)

        return Orientation(
            pitch_deg=math.degrees(orientation['pitch']),
            yaw_deg=math.degrees(orientation['yaw']),
            roll_deg=math.degrees(orientation['roll'])
        )

    def __del__(self):
        for subscription in self.subscriptions:
            self.drone.unsubscribe(subscription)

        self.drone.disconnect()
