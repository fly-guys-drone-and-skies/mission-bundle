from proto import GeoLocation3D as GeoLocation3DProto, Orientation as OrientationProto
from type.serde.protobuf import ProtobufSerializable

from dataclasses import dataclass


@dataclass
class GeoLocation3D(ProtobufSerializable):
    latitude_deg: float  # Absolute latitude (degrees)
    longitude_deg: float  # Absolute longitude (degrees)
    altitude_relative_m: float  # Relative altitude (meters)

    def to_protobuf(self) -> GeoLocation3DProto:
        proto = GeoLocation3DProto()
        proto.latitude = self.latitude_deg
        proto.longitude = self.longitude_deg
        proto.altitudeMeters = self.altitude_relative_m

        return proto

    @staticmethod
    def from_protobuf(proto: GeoLocation3DProto) -> 'GeoLocation3D':
        return GeoLocation3D(
            latitude_deg=proto.latitude,
            longitude_deg=proto.longitude,
            altitude_relative_m=proto.altitudeMeters
        )


@dataclass
class Orientation(ProtobufSerializable):

    pitch_deg: float  # Pitch (degrees)
    yaw_deg: float  # Yaw (degrees)
    roll_deg: float  # Roll (degrees)

    def to_protobuf(self) -> OrientationProto:
        proto = OrientationProto()
        proto.pitchDegrees = self.pitch_deg
        proto.yawDegrees = self.yaw_deg
        proto.rollDegrees = self.roll_deg

        return proto

    @staticmethod
    def from_protobuf(proto: OrientationProto) -> 'Orientation':
        return Orientation(
            pitch_deg=proto.pitchDegrees,
            yaw_deg=proto.yawDegrees,
            roll_deg=proto.rollDegrees
        )
