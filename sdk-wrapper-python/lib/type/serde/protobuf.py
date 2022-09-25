from type.serde.serialize import Serializer

from google.protobuf.reflection import GeneratedProtocolMessageType

from abc import abstractmethod


class ProtobufSerializable(Serializer):

    def serialize(self) -> bytes:
        return self.to_protobuf().SerializeToString()

    @abstractmethod
    def to_protobuf(self) -> GeneratedProtocolMessageType: pass

    @staticmethod
    @abstractmethod
    def from_protobuf(proto: GeneratedProtocolMessageType): pass
