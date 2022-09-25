# Schemas

These are the protobuf schemas used both for external swarm communication and IPC.

## Dependencies

The [protobuf compiler](https://github.com/protocolbuffers/protobuf#protocol-compiler-installation) (`protoc`) is required to convert these schemas into auto-generated code.

## Usage

1. Create temporary directory for output (e.g. `mkdir tmp_java`)
2. Compile the schemas of interest (e.g. `protoc -I. --experimental_allow_proto3_optional --java_out=tmp_java --python_out=tmp_python ipc/Command.proto`)
3. Copy the generated code into the proper mission software or SDK wrapper directory.

Currently, auto-generated code should be placed into these directories:

* `mission-java/src/main/java/edu/rit/se/sars/communication/proto`
  * External and IPC
* `sdk-wrapper-python/proto`
  * IPC
