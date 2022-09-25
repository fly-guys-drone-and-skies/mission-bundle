package edu.rit.se.sars.operator;

import edu.rit.se.sars.communication.message.MessageQueueWorker;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelope;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelopeDeserializer;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityRegistry;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.communication.network.protocol.UDPProtocolStrategy;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;
import edu.rit.se.sars.operator.web.OperatorWebServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OperatorApplication {

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(getOptions(), args);

        InetSocketAddress serverBindAddress = getBindAddress(commandLine, "operator-swarm");
        NetworkEntityRegistry<IPEntity> entityRegistry = new NetworkEntityRegistry<>(
            new IPEntity(NetworkEntityType.OPERATOR, serverBindAddress)
        );

        // Setup inbound queue and associated worker
        BlockingQueue<SwarmMessageEnvelope> inboundQueue = new LinkedBlockingQueue<>();
        MessageQueueWorker<SwarmMessageEnvelope> inboundMessageWorker = new MessageQueueWorker<>(inboundQueue);

        // Setup protocol and delivery handler on top of inbound queue
        UDPProtocolStrategy<SwarmMessageEnvelope> protocolStrategy = new UDPProtocolStrategy<>(
            inboundQueue,
            new ProtobufSerializer<>(),
            new SwarmMessageEnvelopeDeserializer(),
            serverBindAddress
        );
        SwarmDeliveryHandler<IPEntity> deliveryHandler = new SwarmDeliveryHandler<>(protocolStrategy, entityRegistry);
        inboundMessageWorker.addSubscriber(deliveryHandler);

        Thread protocolStrategyThread = new Thread(protocolStrategy);
        Thread inboundQueueWorkerThread = new Thread(inboundMessageWorker);
        Thread deliveryHandlerThread = new Thread(deliveryHandler);
        protocolStrategyThread.start();
        inboundQueueWorkerThread.start();
        deliveryHandlerThread.start();

        InetSocketAddress webServerBindAddress = getBindAddress(commandLine, "operator-http");
        Path tmpDir = Files.createTempDirectory("mission");
        OperatorWebServer webServer = new OperatorWebServer(webServerBindAddress, inboundMessageWorker, deliveryHandler, tmpDir);

        protocolStrategyThread.join();
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption("Sh", "operator-swarm-host", true, "Operator swarm server bind host");
        options.addOption("Sp", "operator-swarm-port", true, "Operator swarm server bind port");
        options.addOption("Hh", "operator-http-host", true, "Operator HTTP server bind host");
        options.addOption("Hp", "operator-http-port", true, "Operator HTTP server bind port");

        return options;
    }

    private static InetSocketAddress getBindAddress(CommandLine commandLine, String optionPrefix) {
        String bindHost = commandLine.getOptionValue(optionPrefix + "-host");
        int bindPort = Integer.parseInt(commandLine.getOptionValue(optionPrefix + "-port"));

        return new InetSocketAddress(bindHost, bindPort);
    }
}
