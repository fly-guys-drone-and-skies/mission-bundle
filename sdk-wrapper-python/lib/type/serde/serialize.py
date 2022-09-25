from abc import ABC, abstractmethod


class Serializer(ABC):
    @abstractmethod
    def serialize(self) -> bytes: pass


class Deserializer(ABC):
    @staticmethod
    @abstractmethod
    def deserialize(data: bytes): pass
