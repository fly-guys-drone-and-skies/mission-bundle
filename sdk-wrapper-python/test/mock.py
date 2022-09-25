from type.domain import Orientation, GeoLocation3D
from sdk.drone import Drone

import time
from typing import Optional


class InvocationLogger:
    def __init__(self):
        self.invocations = {}

    def _log_invocation(self, func):
        if func not in self.invocations:
            self.invocations[func] = 0
        self.invocations[func] += 1


class MockDrone(Drone, InvocationLogger):

    def __init__(self):
        Drone.__init__(self)
        InvocationLogger.__init__(self)

    def takeoff(self):
        self._log_invocation(self.takeoff)
        time.sleep(2)

    def land(self):
        self._log_invocation(self.land)
        time.sleep(1)

    def move_to(self, location: GeoLocation3D, heading_deg: Optional[float] = None):
        self._log_invocation(self.move_to)
        time.sleep(3)

    def move_gimbal(self, orientation: Orientation):
        self._log_invocation(self.move_gimbal)

    def set_zoom_level(self, zoom_level: float):
        self._log_invocation(self.set_zoom_level)

    def get_location(self) -> GeoLocation3D:
        return GeoLocation3D(latitude_deg=12.3, longitude_deg=45.6, altitude_relative_m=78.9)

    def get_orientation(self) -> Orientation:
        return Orientation(pitch_deg=13.4, yaw_deg=56.7, roll_deg=89.01)

    def is_connected(self) -> bool:
        return True
