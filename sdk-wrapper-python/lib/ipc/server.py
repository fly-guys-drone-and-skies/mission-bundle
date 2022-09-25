from type.command import *
from type.event import *
from sdk.drone import *

import zmq

from concurrent.futures.thread import ThreadPoolExecutor
import logging
from typing import Optional, Tuple

NUM_PROCESSES = 5


class DroneServer(DroneObserver):
    def __init__(self, bind: Tuple[str, int], drone: Drone):
        """
        :param bind: (host, port) tuple
        """
        self.bind_host, self.bind_port = bind
        self.drone = drone
        self.drone.add_observer(self)

        context = zmq.Context()
        self.socket = context.socket(zmq.PAIR)  # one-to-one

    def listen(self):
        logging.info(f"Starting server {self.bind_host}:{self.bind_port}")
        self.socket.bind("tcp://{0}:{1}".format(self.bind_host, self.bind_port))

        with ThreadPoolExecutor(max_workers=NUM_PROCESSES) as executor:
            while True:
                message = self.socket.recv()
                command = CommandDeserializer.deserialize(message)
                logging.debug(f"Received command: {command}")

                executor.submit(self.handle_command, command)

    def handle_command(self, command: Command):
        command_type = type(command)
        command_response: Optional[DroneEvent] = None

        logging.debug(f"Handling command: {command}")

        try:
            if command_type == LandCommand:
                self.drone.land()
            elif command_type == TakeoffCommand:
                self.drone.takeoff()
            elif command_type == MoveToCommand:
                command: MoveToCommand
                self.drone.move_to(command.to_location, command.heading_deg)
            elif command_type == MoveGimbalCommand:
                command: MoveGimbalCommand
                self.drone.move_gimbal(command.to_orientation)
            elif command_type == ChangeZoomLevelCommand:
                command: ChangeZoomLevelCommand
                self.drone.set_zoom_level(command.zoom_level)
            elif command_type == GetPositionCommand:
                command: GetPositionCommand
                location = self.drone.get_location()

                command_response = PositionStatusEvent(location=location, command_uuid=command.uuid)
            elif command_type == GetOrientationCommand:
                command: GetOrientationCommand
                orientation = self.drone.get_orientation()

                command_response = OrientationStatusEvent(orientation=orientation, command_uuid=command.uuid)
            elif command_type == GetConnectionStateCommand:
                command: GetConnectionStateCommand
                is_connected = self.drone.is_connected()

                command_response = ConnectionStateEvent(is_connected=is_connected, command_uuid=command.uuid)
            else:
                raise NotImplementedError(f"Unsupported command type: {command_type}")

            logging.debug("Completed command")
            if command_response:
                self.notify(command_response)
            else:
                self.notify(CommandCompletedEvent(command.uuid))
        except Exception as e:
            logging.error(f"Could not process command {command_type}, got error: {e}")

    def notify(self, event: DroneEvent):
        logging.debug(f"Sending event: {event}")
        self.socket.send(event.serialize(), flags=zmq.NOBLOCK)
