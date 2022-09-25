from type.domain import GeoLocation3D, Orientation
from proto import Command as CommandProto
from type.serde.protobuf import ProtobufSerializable
from type.serde.serialize import Deserializer

from abc import ABC
from dataclasses import dataclass
from typing import Dict, Optional
from uuid import UUID, uuid4


@dataclass(init=False)
class Command(ProtobufSerializable, ABC):

    uuid: UUID

    def __init__(self, uuid: UUID = None):
        if uuid is None:
            self.uuid = uuid4()
        else:
            self.uuid = uuid

    def get_base_protobuf(self):
        command = CommandProto()
        command.uuid = self.uuid.bytes

        return command


@dataclass(init=False)
class TakeoffCommand(Command):
    def __init__(self, uuid: UUID = None):
        super().__init__(uuid)

    def to_protobuf(self) -> CommandProto:
        command: CommandProto = self.get_base_protobuf()
        takeoff = CommandProto.TakeoffCommand()
        command.takeoff.CopyFrom(takeoff)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto) -> CommandProto:
        return TakeoffCommand(
            uuid=UUID(bytes=proto.uuid)
        )


@dataclass(init=False)
class LandCommand(Command):
    def __init__(self, uuid: UUID = None):
        super().__init__(uuid)

    def to_protobuf(self) -> CommandProto:
        command: CommandProto = self.get_base_protobuf()
        land = CommandProto.LandCommand()
        command.land.CopyFrom(land)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        return LandCommand(
            uuid=UUID(bytes=proto.uuid)
        )


@dataclass(init=False)
class MoveToCommand(Command):

    to_location: GeoLocation3D  # Location to move to
    heading_deg: Optional[float]

    def __init__(self, to_location: GeoLocation3D, heading_deg: Optional[float] = None, uuid: UUID = None):
        super().__init__(uuid)
        self.to_location = to_location
        self.heading_deg = heading_deg

    def to_protobuf(self) -> CommandProto:
        command = self.get_base_protobuf()
        move_to = CommandProto.MoveToCommand()
        move_to.toLocation.CopyFrom(
            self.to_location.to_protobuf()
        )
        if self.heading_deg is not None:
            move_to.headingDegrees = self.heading_deg
        command.moveTo.CopyFrom(move_to)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        uuid = UUID(bytes=proto.uuid)
        to_location = GeoLocation3D.from_protobuf(
            proto.moveTo.toLocation
        )
        heading_deg = None
        if proto.moveTo.HasField('headingDegrees'):
            heading_deg = proto.moveTo.headingDegrees

        return MoveToCommand(
            uuid=uuid,
            to_location=to_location,
            heading_deg=heading_deg
        )


@dataclass(init=False)
class MoveGimbalCommand(Command):

    to_orientation: Orientation  # absolute orientation to move gimbal to

    def __init__(self, to_orientation: Orientation, uuid: UUID = None):
        super().__init__(uuid)
        self.to_orientation = to_orientation

    def to_protobuf(self) -> CommandProto:
        command = self.get_base_protobuf()
        move_gimbal = CommandProto.MoveGimbalCommand()
        move_gimbal.toOrientation.CopyFrom(
            self.to_orientation.to_protobuf()
        )
        command.moveGimbal.CopyFrom(move_gimbal)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        uuid = UUID(bytes=proto.uuid)
        to_orientation = Orientation.from_protobuf(
            proto.moveGimbal.toOrientation
        )

        return MoveGimbalCommand(
            uuid=uuid,
            to_orientation=to_orientation
        )


@dataclass(init=False)
class ChangeZoomLevelCommand(Command):

    zoom_level: float

    def __init__(self, zoom_level: float, uuid: UUID = None):
        super().__init__(uuid)
        self.zoom_level = zoom_level

    def to_protobuf(self) -> CommandProto:
        command = self.get_base_protobuf()
        change_zoom_level = CommandProto.ChangeZoomLevelCommand()
        change_zoom_level.zoomLevel = self.zoom_level
        command.changeZoomLevel.CopyFrom(change_zoom_level)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        uuid = UUID(bytes=proto.uuid)
        zoom_level = proto.changeZoomLevel.zoomLevel

        return ChangeZoomLevelCommand(
            uuid=uuid,
            zoom_level=zoom_level
        )


@dataclass(init=False)
class GetPositionCommand(Command):

    def __init__(self, uuid: UUID = None):
        super().__init__(uuid)

    def to_protobuf(self) -> CommandProto:
        command: CommandProto = self.get_base_protobuf()
        get_position = CommandProto.GetPositionCommand()
        command.getPosition.CopyFrom(get_position)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        return GetPositionCommand(
            uuid=UUID(bytes=proto.uuid)
        )


@dataclass(init=False)
class GetOrientationCommand(Command):

    def __init__(self, uuid: UUID = None):
        super().__init__(uuid)

    def to_protobuf(self) -> CommandProto:
        command: CommandProto = self.get_base_protobuf()
        get_orientation = CommandProto.GetOrientationCommand()
        command.getOrientation.CopyFrom(get_orientation)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        return GetOrientationCommand(
            uuid=UUID(bytes=proto.uuid)
        )


@dataclass(init=False)
class GetConnectionStateCommand(Command):

    def __init__(self, uuid: UUID = None):
        super().__init__(uuid)

    def to_protobuf(self) -> CommandProto:
        command: CommandProto = self.get_base_protobuf()
        get_position = CommandProto.GetConnectionStateCommand()
        command.getConnectionState.CopyFrom(get_position)

        return command

    @staticmethod
    def from_protobuf(proto: CommandProto):
        return GetConnectionStateCommand(
            uuid=UUID(bytes=proto.uuid)
        )


class CommandDeserializer(Deserializer):
    commands: Dict[str, Command] = {
        "land": LandCommand,
        "takeoff": TakeoffCommand,
        "moveTo": MoveToCommand,
        "moveGimbal": MoveGimbalCommand,
        "changeZoomLevel": ChangeZoomLevelCommand,
        "getPosition": GetPositionCommand,
        "getOrientation": GetOrientationCommand,
        "getConnectionState": GetConnectionStateCommand
    }

    @staticmethod
    def deserialize(proto_bytes: bytes):
        proto = CommandProto()
        proto.ParseFromString(proto_bytes)

        command_type = proto.WhichOneof("command")
        if command_type in CommandDeserializer.commands:
            return CommandDeserializer.commands[command_type].from_protobuf(proto)
        else:
            raise NotImplementedError(f"Unsupported message type: {command_type}")
