import olympe
from olympe.messages.ardrone3.Piloting import TakeOff, moveTo, Landing
from olympe.messages.ardrone3.PilotingState import FlyingStateChanged, moveToChanged, PositionChanged
from olympe.enums.ardrone3.Piloting import MoveTo_Orientation_mode


import time

import sys

DRONE_IP = "192.168.53.1"

float_tol = (1e-5,1e-9)
# home
lat, lon, alt = 43.01532003735721, -77.56665309673676, 91

if __name__ == "__main__":
    drone = olympe.Drone(DRONE_IP)
    drone.connect()
    assert drone(
        moveTo(
            latitude=lat,
            longitude=lon,
            altitude=alt,  # Relative to takeoff altitude
            orientation_mode=MoveTo_Orientation_mode.TO_TARGET,
            heading=0.0,  # not used unless orientation mode is HEADING_START or HEADING_DURING
            _float_tol=float_tol
        )
        >> moveToChanged(status="DONE", _timeout=60*5, _float_tol=float_tol)
        >> FlyingStateChanged(state="hovering", _timeout=60*5)
    ).wait().success()

    assert drone(Landing()).wait().success()
    drone.disconnect()

