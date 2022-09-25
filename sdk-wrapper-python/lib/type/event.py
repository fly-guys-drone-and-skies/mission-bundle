from type.domain import GeoLocation3D, Orientation
from proto import Event as EventProto
from type.serde.protobuf import ProtobufSerializable
from type.serde.serialize import Deserializer

from abc import ABC
from dataclasses import dataclass
from typing import Dict, Optional
from uuid import UUID


@dataclass(init=False)
class DroneEvent(ProtobufSerializable, ABC):

    command_uuid: Optional[UUID]

    def __init__(self, command_uuid: Optional[UUID]):
        self.command_uuid = command_uuid

    def get_base_protobuf(self):
        event = EventProto()

        if self.command_uuid:
            event.commandUUID = self.command_uuid.bytes

        return event


@dataclass(init=False)
class CommandCompletedEvent(DroneEvent):

    def __init__(self, command_uuid: UUID):
        super().__init__(command_uuid)

    def to_protobuf(self) -> EventProto:
        event: EventProto = self.get_base_protobuf()
        command_completed = EventProto.CommandCompletedEvent()

        event.commandCompleted.CopyFrom(command_completed)

        return event

    @staticmethod
    def from_protobuf(proto: EventProto) -> EventProto:
        command_uuid = None
        if proto.HasField('commandUUID'):
            command_uuid = UUID(bytes=proto.commandUUID)

        return CommandCompletedEvent(
            command_uuid=command_uuid
        )


@dataclass(init=False)
class BatteryStatusEvent(DroneEvent):

    percentage_remaining: float

    def __init__(self, percentage_remaining: float, command_uuid: Optional[UUID] = None):
        super().__init__(command_uuid)
        self.percentage_remaining = percentage_remaining

    def to_protobuf(self) -> EventProto:
        event: EventProto = self.get_base_protobuf()
        battery_status = EventProto.BatteryStatusEvent()
        battery_status.percentageRemaining = self.percentage_remaining
        event.batteryStatus.CopyFrom(battery_status)

        return event

    @staticmethod
    def from_protobuf(proto: EventProto) -> EventProto:
        command_uuid = None
        if proto.HasField('commandUUID'):
            command_uuid = UUID(bytes=proto.commandUUID)

        return BatteryStatusEvent(
            percentage_remaining=proto.batteryStatus.percentageRemaining,
            command_uuid=command_uuid
        )


@dataclass(init=False)
class PositionStatusEvent(DroneEvent):

    location: GeoLocation3D

    def __init__(self, location: GeoLocation3D, command_uuid: Optional[UUID] = None):
        super().__init__(command_uuid)
        self.location = location

    def to_protobuf(self) -> EventProto:
        event: EventProto = self.get_base_protobuf()
        position_status = EventProto.PositionStatusEvent()
        position_status.location.CopyFrom(
            self.location.to_protobuf()
        )
        event.positionStatus.CopyFrom(position_status)

        return event

    @staticmethod
    def from_protobuf(proto: EventProto) -> EventProto:
        command_uuid = None
        if proto.HasField('commandUUID'):
            command_uuid = UUID(bytes=proto.commandUUID)

        return PositionStatusEvent(
            location=GeoLocation3D.from_protobuf(
                proto.positionStatus.location
            ),
            command_uuid=command_uuid
        )


@dataclass(init=False)
class OrientationStatusEvent(DroneEvent):

    orientation: Orientation

    def __init__(self, orientation: Orientation, command_uuid: Optional[UUID] = None):
        super().__init__(command_uuid)
        self.orientation = orientation

    def to_protobuf(self) -> EventProto:
        event: EventProto = self.get_base_protobuf()
        orientation_status = EventProto.OrientationStatusEvent()
        orientation_status.orientation.CopyFrom(
            self.orientation.to_protobuf()
        )
        event.orientationStatus.CopyFrom(orientation_status)

        return event

    @staticmethod
    def from_protobuf(proto: EventProto) -> EventProto:
        command_uuid = None
        if proto.HasField('commandUUID'):
            command_uuid = UUID(bytes=proto.commandUUID)

        return OrientationStatusEvent(
            orientation=Orientation.from_protobuf(
                proto.orientationStatus.orientation
            ),
            command_uuid=command_uuid
        )


@dataclass(init=False)
class ConnectionStateEvent(DroneEvent):

    is_connected: bool

    def __init__(self, is_connected: bool, command_uuid: Optional[UUID] = None):
        super().__init__(command_uuid)

        self.is_connected = is_connected

    def to_protobuf(self) -> EventProto:
        event: EventProto = self.get_base_protobuf()
        connection_state = EventProto.ConnectionStateEvent()
        connection_state.isConnected = self.is_connected
        event.connectionState.CopyFrom(connection_state)

        return event

    @staticmethod
    def from_protobuf(proto: EventProto) -> EventProto:
        command_uuid = None
        if proto.HasField('commandUUID'):
            command_uuid = UUID(bytes=proto.commandUUID)

        return ConnectionStateEvent(
            is_connected=proto.connectionState.isConnected,
            command_uuid=command_uuid
        )


class DroneEventDeserializer(Deserializer):
    events: Dict[str, DroneEvent] = {
        "commandCompleted": CommandCompletedEvent,
        "batteryStatus": BatteryStatusEvent,
        "positionStatus": PositionStatusEvent,
        "orientationStatus": OrientationStatusEvent,
        "connectionState": ConnectionStateEvent
    }

    @staticmethod
    def deserialize(proto_bytes: bytes):
        proto = EventProto()
        proto.ParseFromString(proto_bytes)

        event_type = proto.WhichOneof("event")
        if event_type in DroneEventDeserializer.events:
            return DroneEventDeserializer.events[event_type].from_protobuf(proto)
        else:
            raise NotImplementedError(f"Unsupported message type: {event_type}")
