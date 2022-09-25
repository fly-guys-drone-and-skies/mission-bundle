package edu.rit.se.sars.mission;

import com.google.common.collect.ImmutableSet;
import edu.rit.se.sars.communication.message.MessageQueueWorker;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelope;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelopeDeserializer;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityRegistry;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.communication.network.protocol.UDPProtocolStrategy;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;
import edu.rit.se.sars.drone.DjiMavicDrone;
import edu.rit.se.sars.drone.Drone;
import edu.rit.se.sars.drone.ParrotAnafiDrone;
import edu.rit.se.sars.mission.consensus.SwarmCoordinator;
import edu.rit.se.sars.mission.consensus.SwarmStatusMonitor;
import edu.rit.se.sars.mission.flight.FlightPathController;
import edu.rit.se.sars.mission.flight.interrupt.PathInterruptController;
import edu.rit.se.sars.mission.search.decompose.PolygonCellDecomposer;
import edu.rit.se.sars.mission.search.partition.WeightedPartitionAlgorithm;
import edu.rit.se.sars.mission.search.routing.LinKernighanAlgorithm;
import edu.rit.se.sars.mission.target.detection.OkuTargetIdentifier;
import edu.rit.se.sars.mission.target.detection.TargetIdentifier;
import edu.rit.se.sars.mission.target.tracking.FOVTrackingTaskCalculator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MissionApplication {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(getOptions(), args);

        InetSocketAddress serverBindAddress = getBindAddress(commandLine);

        // Setup networking
        NetworkEntityRegistry<IPEntity> entityRegistry = new NetworkEntityRegistry<>(
            new IPEntity(NetworkEntityType.DRONE, serverBindAddress)
        );

        BlockingQueue<SwarmMessageEnvelope> inboundQueue = new LinkedBlockingQueue<>();

        UDPProtocolStrategy<SwarmMessageEnvelope> protocolStrategy = new UDPProtocolStrategy<>(
            inboundQueue,
            new ProtobufSerializer<>(),
            new SwarmMessageEnvelopeDeserializer(),
            serverBindAddress
        );
        SwarmDeliveryHandler<IPEntity> deliveryHandler = new SwarmDeliveryHandler<>(protocolStrategy, entityRegistry);

        SwarmStatusMonitor<IPEntity> swarmStatusMonitor = new SwarmStatusMonitor<>(deliveryHandler);

        // Setup drone subsystems
        Drone drone = getDrone(commandLine);

        FlightPathController flightPathController = new FlightPathController(drone);

        TargetIdentifier targetIdentifier = new OkuTargetIdentifier(drone);

        SwarmCoordinator<IPEntity> swarmCoordinator = new SwarmCoordinator<>(
            deliveryHandler,
            entityRegistry,
            swarmStatusMonitor,
            new PolygonCellDecomposer(),
            new WeightedPartitionAlgorithm(),
            new LinKernighanAlgorithm(),
            flightPathController,
            targetIdentifier,
            new FOVTrackingTaskCalculator()
        );

        PathInterruptController pathInterruptController = new PathInterruptController(swarmCoordinator);
        drone.addSubscriber(pathInterruptController);

        MessageQueueWorker<SwarmMessageEnvelope> inboundMessageWorker = new MessageQueueWorker<>(inboundQueue);
        inboundMessageWorker.addSubscriber(deliveryHandler);
        inboundMessageWorker.addSubscriber(swarmCoordinator);
        inboundMessageWorker.addSubscriber(swarmStatusMonitor);


        Thread protocolStrategyThread = new Thread(protocolStrategy);
        Thread inboundQueueWorkerThread = new Thread(inboundMessageWorker);
        Thread deliveryHandlerThread = new Thread(deliveryHandler);
        Thread swarmStatusMonitorThread = new Thread(swarmStatusMonitor);
        Thread droneThread = new Thread(drone);
        Thread targetIdentifierThread = new Thread(targetIdentifier);

        protocolStrategyThread.start();
        inboundQueueWorkerThread.start();
        deliveryHandlerThread.start();
        swarmStatusMonitorThread.start();
        droneThread.start();
        targetIdentifierThread.start();

        protocolStrategyThread.join();
    }

    private static InetSocketAddress getBindAddress(CommandLine commandLine) {
        String bindHost = commandLine.getOptionValue("swarm-host");
        int bindPort = Integer.parseInt(commandLine.getOptionValue("swarm-port"));

        return new InetSocketAddress(bindHost, bindPort);
    }

    private static Drone getDrone(CommandLine commandLine) throws Exception {
        String droneType = commandLine.getOptionValue("drone-type");
        Set<String> supportedDroneTypes = ImmutableSet.of(ParrotAnafiDrone.droneName,DjiMavicDrone.droneName);
        if (supportedDroneTypes.contains(droneType)) {
            String droneHost = commandLine.getOptionValue("drone-host");
            String wrapperHost = commandLine.getOptionValue("wrapper-host");
            int wrapperPort = Integer.parseInt(commandLine.getOptionValue("wrapper-port"));

            InetAddress droneAddress = InetAddress.getByName(droneHost);
            InetSocketAddress wrapperAddress = new InetSocketAddress(wrapperHost, wrapperPort);
            if (droneType.equals(ParrotAnafiDrone.droneName)) {
                return new ParrotAnafiDrone(droneAddress, wrapperAddress);
            } else if (droneType.equals(DjiMavicDrone.droneName)) {
                return new DjiMavicDrone(droneAddress, wrapperAddress);
            } else {
                throw new IllegalArgumentException(
                        String.format("Unsupported drone type: %s", droneType)
                );
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported drone type: %s", droneType)
            );
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption("Sh", "swarm-host", true, "Drone swarm server bind host");
        options.addOption("Sp", "swarm-port", true, "Drone swarm server bind port");
        options.addOption("Dt", "drone-type", true, "drone type");
        options.addOption("Dh", "drone-host", true, "IP of drone or controller");
        options.addOption("Wh", "wrapper-host", true, "SDK wrapper server IP");
        options.addOption("Wp", "wrapper-port", true, "SDK wrapper server port");

        return options;
    }
}
