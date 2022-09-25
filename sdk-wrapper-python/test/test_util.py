from type.serde.serialize import Deserializer
from type.serde.protobuf import ProtobufSerializable

from uuid import UUID, uuid4


def check_protobuf_serde(obj: ProtobufSerializable, deserializer: Deserializer = None):
    proto = obj.to_protobuf()
    deserialized = obj.from_protobuf(proto)
    assert obj == deserialized

    if deserializer:
        assert deserializer.deserialize(obj.serialize()) == obj


def get_random_uuid() -> UUID:
    return uuid4()
