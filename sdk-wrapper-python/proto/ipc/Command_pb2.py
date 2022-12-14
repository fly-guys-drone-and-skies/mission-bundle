# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: ipc/Command.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from common import Domain_pb2 as common_dot_Domain__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='ipc/Command.proto',
  package='',
  syntax='proto3',
  serialized_options=b'\n/edu.rit.se.sars.communication.proto.ipc.commandP\001',
  create_key=_descriptor._internal_create_key,
  serialized_pb=b'\n\x11ipc/Command.proto\x1a\x13\x63ommon/Domain.proto\"\xf5\x05\n\x07\x43ommand\x12\x0c\n\x04uuid\x18\x01 \x01(\x0c\x12*\n\x07takeoff\x18\x02 \x01(\x0b\x32\x17.Command.TakeoffCommandH\x00\x12$\n\x04land\x18\x03 \x01(\x0b\x32\x14.Command.LandCommandH\x00\x12(\n\x06moveTo\x18\x04 \x01(\x0b\x32\x16.Command.MoveToCommandH\x00\x12\x30\n\nmoveGimbal\x18\x05 \x01(\x0b\x32\x1a.Command.MoveGimbalCommandH\x00\x12:\n\x0f\x63hangeZoomLevel\x18\x06 \x01(\x0b\x32\x1f.Command.ChangeZoomLevelCommandH\x00\x12\x32\n\x0bgetPosition\x18\x07 \x01(\x0b\x32\x1b.Command.GetPositionCommandH\x00\x12\x38\n\x0egetOrientation\x18\x08 \x01(\x0b\x32\x1e.Command.GetOrientationCommandH\x00\x12@\n\x12getConnectionState\x18\t \x01(\x0b\x32\".Command.GetConnectionStateCommandH\x00\x1a\x10\n\x0eTakeoffCommand\x1a\r\n\x0bLandCommand\x1a\x63\n\rMoveToCommand\x12\"\n\ntoLocation\x18\x01 \x01(\x0b\x32\x0e.GeoLocation3D\x12\x1b\n\x0eheadingDegrees\x18\x02 \x01(\x01H\x00\x88\x01\x01\x42\x11\n\x0f_headingDegrees\x1a\x38\n\x11MoveGimbalCommand\x12#\n\rtoOrientation\x18\x01 \x01(\x0b\x32\x0c.Orientation\x1a+\n\x16\x43hangeZoomLevelCommand\x12\x11\n\tzoomLevel\x18\x01 \x01(\x01\x1a\x14\n\x12GetPositionCommand\x1a\x17\n\x15GetOrientationCommand\x1a\x1b\n\x19GetConnectionStateCommandB\t\n\x07\x63ommandB3\n/edu.rit.se.sars.communication.proto.ipc.commandP\x01\x62\x06proto3'
  ,
  dependencies=[common_dot_Domain__pb2.DESCRIPTOR,])




_COMMAND_TAKEOFFCOMMAND = _descriptor.Descriptor(
  name='TakeoffCommand',
  full_name='Command.TakeoffCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=478,
  serialized_end=494,
)

_COMMAND_LANDCOMMAND = _descriptor.Descriptor(
  name='LandCommand',
  full_name='Command.LandCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=496,
  serialized_end=509,
)

_COMMAND_MOVETOCOMMAND = _descriptor.Descriptor(
  name='MoveToCommand',
  full_name='Command.MoveToCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='toLocation', full_name='Command.MoveToCommand.toLocation', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='headingDegrees', full_name='Command.MoveToCommand.headingDegrees', index=1,
      number=2, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
    _descriptor.OneofDescriptor(
      name='_headingDegrees', full_name='Command.MoveToCommand._headingDegrees',
      index=0, containing_type=None,
      create_key=_descriptor._internal_create_key,
    fields=[]),
  ],
  serialized_start=511,
  serialized_end=610,
)

_COMMAND_MOVEGIMBALCOMMAND = _descriptor.Descriptor(
  name='MoveGimbalCommand',
  full_name='Command.MoveGimbalCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='toOrientation', full_name='Command.MoveGimbalCommand.toOrientation', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=612,
  serialized_end=668,
)

_COMMAND_CHANGEZOOMLEVELCOMMAND = _descriptor.Descriptor(
  name='ChangeZoomLevelCommand',
  full_name='Command.ChangeZoomLevelCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='zoomLevel', full_name='Command.ChangeZoomLevelCommand.zoomLevel', index=0,
      number=1, type=1, cpp_type=5, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=670,
  serialized_end=713,
)

_COMMAND_GETPOSITIONCOMMAND = _descriptor.Descriptor(
  name='GetPositionCommand',
  full_name='Command.GetPositionCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=715,
  serialized_end=735,
)

_COMMAND_GETORIENTATIONCOMMAND = _descriptor.Descriptor(
  name='GetOrientationCommand',
  full_name='Command.GetOrientationCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=737,
  serialized_end=760,
)

_COMMAND_GETCONNECTIONSTATECOMMAND = _descriptor.Descriptor(
  name='GetConnectionStateCommand',
  full_name='Command.GetConnectionStateCommand',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=762,
  serialized_end=789,
)

_COMMAND = _descriptor.Descriptor(
  name='Command',
  full_name='Command',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  create_key=_descriptor._internal_create_key,
  fields=[
    _descriptor.FieldDescriptor(
      name='uuid', full_name='Command.uuid', index=0,
      number=1, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=b"",
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='takeoff', full_name='Command.takeoff', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='land', full_name='Command.land', index=2,
      number=3, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='moveTo', full_name='Command.moveTo', index=3,
      number=4, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='moveGimbal', full_name='Command.moveGimbal', index=4,
      number=5, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='changeZoomLevel', full_name='Command.changeZoomLevel', index=5,
      number=6, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='getPosition', full_name='Command.getPosition', index=6,
      number=7, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='getOrientation', full_name='Command.getOrientation', index=7,
      number=8, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
    _descriptor.FieldDescriptor(
      name='getConnectionState', full_name='Command.getConnectionState', index=8,
      number=9, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR,  create_key=_descriptor._internal_create_key),
  ],
  extensions=[
  ],
  nested_types=[_COMMAND_TAKEOFFCOMMAND, _COMMAND_LANDCOMMAND, _COMMAND_MOVETOCOMMAND, _COMMAND_MOVEGIMBALCOMMAND, _COMMAND_CHANGEZOOMLEVELCOMMAND, _COMMAND_GETPOSITIONCOMMAND, _COMMAND_GETORIENTATIONCOMMAND, _COMMAND_GETCONNECTIONSTATECOMMAND, ],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
    _descriptor.OneofDescriptor(
      name='command', full_name='Command.command',
      index=0, containing_type=None,
      create_key=_descriptor._internal_create_key,
    fields=[]),
  ],
  serialized_start=43,
  serialized_end=800,
)

_COMMAND_TAKEOFFCOMMAND.containing_type = _COMMAND
_COMMAND_LANDCOMMAND.containing_type = _COMMAND
_COMMAND_MOVETOCOMMAND.fields_by_name['toLocation'].message_type = common_dot_Domain__pb2._GEOLOCATION3D
_COMMAND_MOVETOCOMMAND.containing_type = _COMMAND
_COMMAND_MOVETOCOMMAND.oneofs_by_name['_headingDegrees'].fields.append(
  _COMMAND_MOVETOCOMMAND.fields_by_name['headingDegrees'])
_COMMAND_MOVETOCOMMAND.fields_by_name['headingDegrees'].containing_oneof = _COMMAND_MOVETOCOMMAND.oneofs_by_name['_headingDegrees']
_COMMAND_MOVEGIMBALCOMMAND.fields_by_name['toOrientation'].message_type = common_dot_Domain__pb2._ORIENTATION
_COMMAND_MOVEGIMBALCOMMAND.containing_type = _COMMAND
_COMMAND_CHANGEZOOMLEVELCOMMAND.containing_type = _COMMAND
_COMMAND_GETPOSITIONCOMMAND.containing_type = _COMMAND
_COMMAND_GETORIENTATIONCOMMAND.containing_type = _COMMAND
_COMMAND_GETCONNECTIONSTATECOMMAND.containing_type = _COMMAND
_COMMAND.fields_by_name['takeoff'].message_type = _COMMAND_TAKEOFFCOMMAND
_COMMAND.fields_by_name['land'].message_type = _COMMAND_LANDCOMMAND
_COMMAND.fields_by_name['moveTo'].message_type = _COMMAND_MOVETOCOMMAND
_COMMAND.fields_by_name['moveGimbal'].message_type = _COMMAND_MOVEGIMBALCOMMAND
_COMMAND.fields_by_name['changeZoomLevel'].message_type = _COMMAND_CHANGEZOOMLEVELCOMMAND
_COMMAND.fields_by_name['getPosition'].message_type = _COMMAND_GETPOSITIONCOMMAND
_COMMAND.fields_by_name['getOrientation'].message_type = _COMMAND_GETORIENTATIONCOMMAND
_COMMAND.fields_by_name['getConnectionState'].message_type = _COMMAND_GETCONNECTIONSTATECOMMAND
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['takeoff'])
_COMMAND.fields_by_name['takeoff'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['land'])
_COMMAND.fields_by_name['land'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['moveTo'])
_COMMAND.fields_by_name['moveTo'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['moveGimbal'])
_COMMAND.fields_by_name['moveGimbal'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['changeZoomLevel'])
_COMMAND.fields_by_name['changeZoomLevel'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['getPosition'])
_COMMAND.fields_by_name['getPosition'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['getOrientation'])
_COMMAND.fields_by_name['getOrientation'].containing_oneof = _COMMAND.oneofs_by_name['command']
_COMMAND.oneofs_by_name['command'].fields.append(
  _COMMAND.fields_by_name['getConnectionState'])
_COMMAND.fields_by_name['getConnectionState'].containing_oneof = _COMMAND.oneofs_by_name['command']
DESCRIPTOR.message_types_by_name['Command'] = _COMMAND
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

Command = _reflection.GeneratedProtocolMessageType('Command', (_message.Message,), {

  'TakeoffCommand' : _reflection.GeneratedProtocolMessageType('TakeoffCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_TAKEOFFCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.TakeoffCommand)
    })
  ,

  'LandCommand' : _reflection.GeneratedProtocolMessageType('LandCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_LANDCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.LandCommand)
    })
  ,

  'MoveToCommand' : _reflection.GeneratedProtocolMessageType('MoveToCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_MOVETOCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.MoveToCommand)
    })
  ,

  'MoveGimbalCommand' : _reflection.GeneratedProtocolMessageType('MoveGimbalCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_MOVEGIMBALCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.MoveGimbalCommand)
    })
  ,

  'ChangeZoomLevelCommand' : _reflection.GeneratedProtocolMessageType('ChangeZoomLevelCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_CHANGEZOOMLEVELCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.ChangeZoomLevelCommand)
    })
  ,

  'GetPositionCommand' : _reflection.GeneratedProtocolMessageType('GetPositionCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_GETPOSITIONCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.GetPositionCommand)
    })
  ,

  'GetOrientationCommand' : _reflection.GeneratedProtocolMessageType('GetOrientationCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_GETORIENTATIONCOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.GetOrientationCommand)
    })
  ,

  'GetConnectionStateCommand' : _reflection.GeneratedProtocolMessageType('GetConnectionStateCommand', (_message.Message,), {
    'DESCRIPTOR' : _COMMAND_GETCONNECTIONSTATECOMMAND,
    '__module__' : 'ipc.Command_pb2'
    # @@protoc_insertion_point(class_scope:Command.GetConnectionStateCommand)
    })
  ,
  'DESCRIPTOR' : _COMMAND,
  '__module__' : 'ipc.Command_pb2'
  # @@protoc_insertion_point(class_scope:Command)
  })
_sym_db.RegisterMessage(Command)
_sym_db.RegisterMessage(Command.TakeoffCommand)
_sym_db.RegisterMessage(Command.LandCommand)
_sym_db.RegisterMessage(Command.MoveToCommand)
_sym_db.RegisterMessage(Command.MoveGimbalCommand)
_sym_db.RegisterMessage(Command.ChangeZoomLevelCommand)
_sym_db.RegisterMessage(Command.GetPositionCommand)
_sym_db.RegisterMessage(Command.GetOrientationCommand)
_sym_db.RegisterMessage(Command.GetConnectionStateCommand)


DESCRIPTOR._options = None
# @@protoc_insertion_point(module_scope)
