from type.domain import Orientation, GeoLocation3D
from type.event import DroneEvent

from abc import ABC, abstractmethod
from typing import Optional, Set


class DroneObserver(ABC):

    @abstractmethod
    def notify(self, event: DroneEvent): pass


class Drone(ABC):

    def __init__(self):
        self.observers: Set[DroneObserver] = set()

    def add_observer(self, observer: DroneObserver):
        """
        Add event observer
        :param observer: Observer to add
        """
        self.observers.add(observer)

    def event(self, event: DroneEvent):
        """
        Publish specified event to observers
        :param event: Event to publish
        """
        for observer in self.observers:
            observer.notify(event)

    @abstractmethod
    def is_connected(self) -> bool:
        """
        Get SDK's current connection state with drone
        :return: True if SDK is connected to drone, false otherwise
        """
        pass

    @abstractmethod
    def takeoff(self):
        """
        Takeoff and hover at current position
        """
        pass

    @abstractmethod
    def land(self):
        """
        Land drone at current position
        """
        pass

    @abstractmethod
    def move_to(self, location: GeoLocation3D, heading_deg: Optional[float] = None):
        """
        Fly drone to specified location
        :param location: Location to fly to
        :param heading_deg: Heading to maintain during movement, relative to north.
            If not specified, drone shall look toward end location.
        """
        pass

    @abstractmethod
    def move_gimbal(self, orientation: Orientation):
        """
        Move gimbal to specified orientation
        :param orientation: New gimbal orientation
        """
        pass

    @abstractmethod
    def set_zoom_level(self, zoom_level: float):
        """
        Set camera zoom level
        :param zoom_level: New zoom level (1.0 = no zoom)
        """
        pass

    @abstractmethod
    def get_location(self) -> GeoLocation3D:
        """
        :return: Drone's current location
        """
        pass

    @abstractmethod
    def get_orientation(self) -> Orientation:
        """
        Get drone's current orientation
        :return: Orientation relative to north
        """
        pass
