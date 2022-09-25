package edu.rit.se.sars.drone;

import com.google.common.collect.ImmutableSet;
import edu.rit.se.sars.communication.ipc.ProtoQueuedClient;
import edu.rit.se.sars.communication.ipc.SocketClient;
import edu.rit.se.sars.communication.ipc.ZMQClient;
import edu.rit.se.sars.communication.message.MessagePublisher;
import edu.rit.se.sars.communication.message.internal.command.*;
import edu.rit.se.sars.communication.message.internal.event.*;
import edu.rit.se.sars.communication.proto.ipc.command.RouteOuterClass;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;
import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;

import edu.rit.se.sars.drone.exception.DroneCommandException;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Drone hardware interface
 * Runnable for the case of drones where sensor status must be polled or listened for in IPC
 */
public abstract class Drone extends MessagePublisher<DroneEvent> implements Runnable {

    static {
        nu.pattern.OpenCV.loadLocally();
    }
    private final String routeUUID = "58e0a7d7-eebc-11d8-9669-0800200c9a66";
    private final InetAddress droneAddress;
    private final ProtoQueuedClient<ProtobufSerializable<?>, DroneEvent> messageClient;
    private final Logger logger;
    private final Map<UUID, CompletableFuture<DroneEvent>> outboundRequests = new HashMap<>();

    public Drone(InetAddress droneAddress, InetSocketAddress wrapperAddress, Class droneClass) {
        this.droneAddress = droneAddress;

        //Hardcoded class -> client set.
        Set<Class> zmqClientClasses = ImmutableSet.of(ParrotAnafiDrone.class);
        Set<Class> socketClientClasses = ImmutableSet.of(DjiMavicDrone.class);
        if (zmqClientClasses.contains(droneClass)) {
            this.messageClient = new ZMQClient<>(
                    wrapperAddress,
                    new ProtobufSerializer<>(),
                    new DroneEventDeserializer(),
                    this
            );
        } else {
            this.messageClient = new SocketClient<>(
                    wrapperAddress,
                    new ProtobufSerializer<>(),
                    new DroneEventDeserializer(),
                    this
            );
        }

        logger = LoggerFactory.getLogger(droneClass);
    }

    /**
     * Get connection state of drone
     * @return True if connected, false otherwise
     */
    public boolean isConnected() {
        DroneEvent eventResponse = this.sendCommand(new GetConnectionStateCommand());

        if (eventResponse instanceof ConnectionStateEvent) {
            ConnectionStateEvent connectionStateEvent = (ConnectionStateEvent) eventResponse;

            return connectionStateEvent.isConnected();
        } else {
            throw new IllegalStateException("Expected ConnectionStateEvent, got " + eventResponse.getClass());
        }
    }

    /**
     * Takeoff and hover drone at current position
     */
    public void takeoff() {
        logger.debug("Takeoff");
        this.sendCommand(new TakeoffCommand());
    }

    /**
     * Move drone to specified location
     * @param location Location to move to
     * @param headingDegrees Optional heading to maintain in-flight. If not specified, drone will look to specified location.
     */
    public void moveTo(GeoLocation3D location, Optional<Double> headingDegrees) {
        logger.debug("Move to {}", location);
        this.sendCommand(new MoveToCommand(location, headingDegrees));
    }

    /**
     * Move gimbal to specified orientation
     * @param orientation Orientation to move gimbal to
     */
    public void moveGimbalTo(Orientation orientation) {
        logger.debug("Move gimbal to {}", orientation);
        this.sendCommand(new MoveGimbalCommand(orientation));
    }

    /**
     * Land drone at current position
     */
    public void land() {
        logger.debug("Land");
        this.sendCommand(new LandCommand());
    }

    /**
     * Get drone location
     * @return Location of drone
     */
    public GeoLocation3D getLocation() {
        DroneEvent eventResponse = this.sendCommand(new GetPositionCommand());

        if (eventResponse instanceof PositionStatusEvent) {
            PositionStatusEvent positionStatusEvent = (PositionStatusEvent) eventResponse;

            return positionStatusEvent.getLocation();
        } else {
            throw new IllegalStateException("Expected PositionStatusEvent, got " + eventResponse.getClass());
        }
    }

    /**
     * Get orientation of drone
     * @return Orientation of drone, relative to north
     */
    public Orientation getOrientation() {
        DroneEvent eventResponse = this.sendCommand(new GetOrientationCommand());

        if (eventResponse instanceof OrientationStatusEvent) {
            OrientationStatusEvent orientationStatusEvent = (OrientationStatusEvent) eventResponse;

            return orientationStatusEvent.getOrientation();
        } else {
            throw new IllegalStateException("Expected PositionStatusEvent, got " + eventResponse.getClass());
        }
    }

    public InetAddress getDroneAddress () {
        return droneAddress;
    }

    /**
     * @return Drone camera properties
     */
    public abstract CameraProperties getCameraProperties();

    /**
     * @return Live video feed from drone camera
     */
    public abstract VideoCapture getVideoFeed();

    /**
     * Set drone camera zoom level
     * @param zoomLevel New zoom level (1.0 = no zoom)
     */
    public void setZoomLevel(double zoomLevel) {
        logger.debug("Set zoom level to {}", zoomLevel);
        this.sendCommand(new ChangeZoomLevelCommand(zoomLevel));
    }

    public DroneEvent sendCommand(final DroneCommand command) {
        CompletableFuture<DroneEvent> outgoingEvent = new CompletableFuture<>();
        this.outboundRequests.put(command.getUuid(), outgoingEvent);

        try {
            this.messageClient.send(command);
            return outgoingEvent.get();
        } catch (Exception e) {
            logger.error("Failed to send command", e);
            throw new DroneCommandException(e);
        }
    }

    public DroneEvent sendRoute(final RouteOuterClass.Route route) {
        CompletableFuture<DroneEvent> routeEvent = new CompletableFuture<>();
        this.outboundRequests.put(UUID.fromString(routeUUID), routeEvent);
        try {
            this.messageClient.sendRaw(route.toByteArray());
            return routeEvent.get();
        } catch (Exception e) {
            logger.error("Failed to send command", e);
            throw new DroneCommandException(e);
        }
    }

    @Override
    public void publish(DroneEvent event) {
        Optional<UUID> commandUUID = event.getCommandUUID();

        if (commandUUID.isPresent() && this.outboundRequests.containsKey(commandUUID.get())) {
            this.outboundRequests.remove(commandUUID.get()).complete(event);
        } else {
            if (this.outboundRequests.containsKey(UUID.fromString(routeUUID))) {
                this.outboundRequests.remove(UUID.fromString(routeUUID)).complete(event);
            } else {
                super.publish(event);
            }
        }
    }

    @Override
    public void run() {
        this.messageClient.run();
    }
}