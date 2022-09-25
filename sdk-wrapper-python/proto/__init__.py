"""
This is a hacky way to get around this protobuf issue:
https://stackoverflow.com/questions/31843088/isinstance-doesnt-work-when-use-reflection-of-python-protobuf-v2-6-1
TODO: see if there's a better solution
"""

from proto.ipc.Command_pb2 import *
from proto.ipc.Event_pb2 import *

Orientation = common_dot_Domain__pb2.Orientation
GeoLocation3D = common_dot_Domain__pb2.GeoLocation3D
