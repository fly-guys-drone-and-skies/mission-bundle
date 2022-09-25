# Python SDK Wrapper

A library for wrapping python-based drone SDKs.

Supported SDKs:

- Olympe (Parrot)

## Usage

The Olympe environment must be first [installed and sourced](https://developer.parrot.com/docs/olympe/installation.html#system-requirements).

**NOTE**: This component must be launched BEFORE the associated mission software instance.
gene
To launch the IPC server:
```
./run.sh [zmq_bind_port] [parrot_IP]
```

For example:
```
./run.sh 30000 192.168.54.1
```

The port and IPs should be one-to-one for each drone used by the system (i.e. don't reuse ports or IPs). The port
specified here must also be configured as the SDK wrapper port when launching the mission software (e.g. `mission-java`).


## Testing

To run the unit tests:

```bash
./test.sh
```

## Docker

The SDK wrapper can be packaged as a docker image. This has NOT been tested with live flight.

To create the image:
```bash
./publish.sh
```

To run a container for usage with sphinx (running directly on host):
```bash
docker run --network host -it sdk-wrapper-parrot:0.1.0
```

(UNTESTED) To run a container for usage with the SkyController:
```bash
docker run --privileged -it sdk-wrapper-parrot:0.1.0
```

