# mission-java

This is a Java implementation of the swarm mission software.

## Dependencies

Some dependencies must be manually installed for this project. See required versions in `build.gradle`.

* [OR-Tools](https://developers.google.com/optimization/install/java)
* [OpenCV](https://github.com/opencv/opencv)
  * Must be **compiled** with Java and GStreamer support. Installing through a package manager will be insufficient due to
  this requirement.
    * See [here](https://medium.com/@galaktyk01/how-to-build-opencv-with-gstreamer-b11668fa09c) for enabling GStreamer support
    * See [here](https://opencv-java-tutorials.readthedocs.io/en/latest/01-installing-opencv-for-java.html#install-opencv-3-x-under-linux) for enabling Java support (instructions for 3.x and Eclipse, but mostly still applicable)

A [MapQuest API](https://developer.mapquest.com/) key is required for the map component of the operator interface. See `src/main/resources/public/js/map.js`.
Additionally, an internet connection is required to load the map tiles when the interface is first opened, but is not required beyond that.  
 

## IDE Setup

[Lombok](https://projectlombok.org/) is used in this project for code generation. Most popular IDEs support this
through a plugin.

## Usage

### Operator Interface

Program arguments:

```
-Sh $SWARM_SERVER_HOST -Sp $SWARM_SERVER_PORT -Hh $HTTP_HOST -Hp $HTTP_PORT
```

Where:
* `SWARM_SERVER_HOST` is the swarm server IP to use for this instance (e.g. `127.0.0.1` if all instances on same machine)
* `SWARM_SERVER_PORT` is the swarm server port to use for this instance (e.g. `10000` - must be different from other instances on same machine)
* `HTTP_HOST` is the HTTP server bind host to use for the interface (e.g. `127.0.0.1`)
* `HTTP_PORT` is the HTTP server bind port to use for the interface (e.g. `8080`)



### Mission Application

#### JVM Arguments

Several system libraries must be loaded:

```
-Djava.library.path=$OPENCV_DIR/build/lib:$OR_TOOLS_DIR/lib
```

Where:
* `OPENCV_DIR` is the built OpenCV directory root
* `OR_TOOLS_DIR` is the built OR-Tools directory root

#### Program Arguments

To start a drone instance (after SDK side set up, if applicable):
```
-Sh $SWARM_SERVER_HOST -Sp $SWARM_SERVER_PORT -Dt $DRONE_TYPE -Dh $DRONE_HOST -Wh $WRAPPER_HOST-Wp $WRAPPER_PORT
```

Where:
* `SWARM_SERVER_HOST` is the swarm server IP to use for this instance (e.g. `127.0.0.1` if all instances on same machine)
* `SWARM_SERVER_PORT` is the swarm server port to use for this instance (e.g. `20000` - must be different than other instances on same machine)
* `DRONE_TYPE` is the drone type string for the drone being controlled (e.g. `parrot-anafi`)
* `DRONE_HOST` is the drone's direct IP (e.g. `192.168.54.1` for a SkyController-based drone)
* `WRAPPER_HOST` is the drone's SDK wrapper's server host (e.g. `127.0.0.1`, if on same machine)
* `WRAPPER_HOST` is the drone's SDK wrapper's server port (e.g. `30000`)
