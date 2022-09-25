from olympe import Drone, log
from olympe.messages.ardrone3.Piloting import TakeOff, moveTo, Landing
from olympe.messages.ardrone3.PilotingState import FlyingStateChanged, moveToChanged, PositionChanged
from olympe.enums.ardrone3.Piloting import MoveTo_Orientation_mode
from olympe.messages.common.CommonState import BatteryStateChanged


def set_logging_levels(level):
    config = log.get_config(None)
    for logger in config['loggers']:
        config['loggers'][logger]['level'] = level
    log.set_config(config)


def subscribe(drone, events):
    return [drone.subscribe(callback, event) for event, callback in events.items()]


set_logging_levels("WARN")


event_handler = lambda event, _: print(event)


DRONE_IP = "192.168.42.1"


locations = [
    (43.026091, -77.558904, 25),
    (43.027105, -77.559410, 25)
]



if __name__ == "__main__":
    drone = Drone(DRONE_IP)
    drone.connect()

    subscribe(drone, {
        BatteryStateChanged(): event_handler,
        PositionChanged(): event_handler
    })

    
    drone(
        TakeOff() >> FlyingStateChanged(state="hovering", _timeout=30)
    ).wait().success()

    for lat,lon,alt in locations:
        drone(
            moveTo(
                latitude=lat,
                longitude=lon,
                altitude=alt,  # Relative to takeoff altitude
                orientation_mode=MoveTo_Orientation_mode.TO_TARGET,
                heading=0.0  # not used unless orientation mode is HEADING_START or HEADING_DURING
            )
            >> moveToChanged(status="DONE")
            >> FlyingStateChanged(state="hovering", _timeout=60*5)
        ).wait().success()


    drone(Landing()).wait().success()
    drone.disconnect()


