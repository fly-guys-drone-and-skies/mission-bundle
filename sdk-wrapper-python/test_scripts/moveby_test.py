import olympe
from olympe.messages.ardrone3.Piloting import TakeOff, moveBy, Landing
from olympe.messages.ardrone3.PilotingState import FlyingStateChanged

DRONE_IP = "192.168.42.1"


rotations = [
    (0, 0, -20, 0),
    (5, 0, 0, 0),
    (0,-5,0,0),
    (-5,0,0,0),
    (0,5,0,1.2)
]

if __name__ == "__main__":
    drone = olympe.Drone(DRONE_IP)
    drone.connect()
    assert drone(
        TakeOff()
        >> FlyingStateChanged(state="hovering", _timeout=5)
    ).wait().success()
    for x,y,z,o in rotations:
        assert drone(
            moveBy(x, y, z, o)
            >> FlyingStateChanged(state="hovering", _timeout=60)
        ).wait().success()
    assert drone(Landing()).wait().success()
    drone.disconnect()
