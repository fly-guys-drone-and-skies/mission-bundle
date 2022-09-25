import olympe
from olympe.messages.ardrone3.Piloting import TakeOff, moveBy, Landing
from olympe.messages.ardrone3.PilotingState import FlyingStateChanged
import time

import sys

DRONE_IP = sys.argv[1]

if __name__ == "__main__":
    drone = olympe.Drone(DRONE_IP)
    drone.connect()
    assert drone(TakeOff()).wait(_timeout=10).success()
    time.sleep(3)
    assert drone(Landing()).wait().success()
    drone.disconnect()

