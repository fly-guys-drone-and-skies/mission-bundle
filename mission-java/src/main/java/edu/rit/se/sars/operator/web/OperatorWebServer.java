package edu.rit.se.sars.operator.web;

import edu.rit.se.sars.communication.message.MessageQueueWorker;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelope;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.operator.web.service.MissionService;
import spark.Service;

import java.net.InetSocketAddress;
import java.nio.file.Path;

import static spark.Service.ignite;

/**
 * Spark webserver for operator interface
 */
public class OperatorWebServer {

    private final MissionService missionService;
    private final Service server;

    public OperatorWebServer(
        InetSocketAddress bindAddress,
        MessageQueueWorker<SwarmMessageEnvelope> inboundMessageWorker,
        SwarmDeliveryHandler<IPEntity> deliveryHandler,
        Path storageDirectory
    ) {
        this.missionService = new MissionService(deliveryHandler, storageDirectory.toFile());
        inboundMessageWorker.addSubscriber(missionService);

        this.server = createServer(bindAddress);
    }

    private Service createServer(InetSocketAddress bindAddress) {
        spark.Service http = ignite()
                .ipAddress(bindAddress.getHostString())
                .port(bindAddress.getPort())
                .staticFileLocation("/public");

        http.get("/ping", (q, a) -> "pong");

        http.path("/api", () -> {
            this.missionService.addRoutes(http);
        });

        return http;
    }
}
