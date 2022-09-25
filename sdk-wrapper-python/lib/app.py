from ipc.server import DroneServer
from sdk.parrot.anafi import ParrotAnafiDrone

import argparse
from ipaddress import IPv4Address


def parse_args() -> DroneServer:
    parser = argparse.ArgumentParser()
    parser.add_argument("bind-host", help="IPC server bind host", type=str)
    parser.add_argument("bind-port", help="IPC server bind port", type=int)
    parser.add_argument("drone-type", help="Drone type", type=str)
    parser.add_argument("drone-host", help="Drone host", type=str)

    args = parser.parse_args()

    bind = (args.bind_host, args.bind_port)

    drone_type = args.drone_type
    if drone_type == ParrotAnafiDrone.NAME:
        drone = ParrotAnafiDrone(
            IPv4Address(args.drone_host)
        )
    else:
        raise NotImplementedError(f"Unsupported drone type: {drone_type}")

    return DroneServer(
        bind=bind,
        drone=drone
    )


if __name__ == "__main__":
    server = parse_args()
    server.listen()
