package edu.rit.se.sars.operator.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.swarm.*;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.mission.Mission;
import edu.rit.se.sars.mission.target.detection.LocalTargetDetection;
import edu.rit.se.sars.mission.target.detection.TargetType;
import edu.rit.se.sars.operator.web.request.MissionStartRequest;
import edu.rit.se.sars.operator.web.response.JSONTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MissionService implements WebService, MessageHandler<SwarmMessageEnvelope> {

    private static final long PING_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(3);

    private static final Logger logger = LoggerFactory.getLogger(MissionService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final SwarmDeliveryHandler<IPEntity> deliveryHandler;
    private final File tempDir;

    private final Map<InetSocketAddress, CountDownLatch> pendingPings = new HashMap<>();

    private final Map<UUID, Map<Integer, TargetDetectionMessage>> detectionMessageChunks = new HashMap<>();
    private final Map<UUID, LocalTargetDetection> detections = new HashMap<>();

    public MissionService(SwarmDeliveryHandler<IPEntity> deliveryHandler, File tempDir) {
        this.deliveryHandler = deliveryHandler;
        this.tempDir = tempDir;
    }

    @Override
    public void addRoutes(Service http) {
        http.post("/mission", (req, resp) -> {
            MissionStartRequest startRequest = mapper.readValue(req.body(), MissionStartRequest.class);
            logger.debug("Request: {}", startRequest);

            try {
                this.pingDrones(startRequest.getDrones());
            } catch (Exception e) {
                Map<String, String> responseData = new HashMap<>();
                responseData.put("error", e.getMessage());
                return responseData;
            }

            MissionDefinitionMessage missionMessage = new MissionDefinitionMessage(new Mission(
                deliveryHandler.getEntityRegistry().getEntityMap(),
                startRequest.getArea(),
                startRequest.getMinAltitudeMeters(),
                startRequest.getAltitudeSeparationMeters(),
                TargetType.PERSON
            ));

            deliveryHandler.sendMessage(missionMessage);

            return new HashMap<>();
        }, new JSONTransformer());

        http.delete("/mission", (req, res) -> {
            deliveryHandler.sendMessage(new ReturnHomeMessage());

            return new HashMap<>();
        });

        http.get("/mission/detections", (req, res) ->
            new TreeSet<>(this.detections.values()),
            new JSONTransformer()
        );

        http.get("/mission/detections/:id", (req, res) -> {
            String detectionID = req.params("id");
            File detectionFile =  this.detections.get(UUID.fromString(detectionID)).getFile();

            try (OutputStream out = res.raw().getOutputStream()) {
                Path path = detectionFile.toPath();
                Files.copy(path, out);

                out.flush();
            } catch (IOException e) {
                logger.error("Failed to read image file", e);
            }

            return null;
        });

        http.post("/mission/detections/:id/focus", (req, res) -> {
            UUID detectionUUID = UUID.fromString(req.params("id"));

            deliveryHandler.sendMessage(
                new FocusDetectionMessage(detectionUUID),
                NetworkEntityType.DRONE
            );

            return null;
        });
    }

    private void pingDrones(List<InetSocketAddress> droneAddresses) throws Exception {
        for (InetSocketAddress droneAddress : droneAddresses) {
            IPEntity droneEntity = new IPEntity(NetworkEntityType.DRONE, droneAddress);

            // TODO: countdown doesn't tell if timeout or not
            CountDownLatch countDownLatch = new CountDownLatch(1);
            pendingPings.put(droneAddress, countDownLatch);

            deliveryHandler.sendMessage(new PingMessage(), droneEntity);

            boolean receivedPing = countDownLatch.await(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            if (!receivedPing) {
                throw new TimeoutException(String.format("%s unreachable", droneAddress));
            }
        }
    }

    @Override
    public void handleMessage(SwarmMessageEnvelope envelope) {
        SwarmMessage message = envelope.getMessage();
        logger.debug("Handling message: {}", message);

        if (message instanceof AcknowledgementMessage) {
            NetworkEntity fromEntity = envelope.getFromEntity();
            if (fromEntity instanceof IPEntity) {
                IPEntity fromIPEntity = (IPEntity) fromEntity;

                Optional.ofNullable(
                    this.pendingPings.remove(fromIPEntity.getAddress())
                ).ifPresent(CountDownLatch::countDown);
            } else {
                throw new UnsupportedOperationException(
                    String.format("Unsupported entity type: %s", fromEntity.getClass())
                );
            }
        } else if (message instanceof TargetDetectionMessage) {
            TargetDetectionMessage targetDetectionMessage = (TargetDetectionMessage) message;

            synchronized (this.detectionMessageChunks) {
                this.detectionMessageChunks.computeIfAbsent(targetDetectionMessage.getDetectionUUID(), k -> new HashMap<>());

                Map<Integer, TargetDetectionMessage> chunkMap = this.detectionMessageChunks.get(targetDetectionMessage.getDetectionUUID());
                chunkMap.put(targetDetectionMessage.getChunkIndex(), targetDetectionMessage);

                if (chunkMap.size() == targetDetectionMessage.getNumChunks()) {
                    logger.debug("Full target detection image received");

                    // Received all chunks, don't need them in memory anymore
                    this.detectionMessageChunks.remove(targetDetectionMessage.getDetectionUUID());

                    // Construct file from chunks
                    try {
                        LocalTargetDetection localDetection = LocalTargetDetection.fromTargetDetectionMessages(chunkMap, this.tempDir);
                        this.detections.put(localDetection.getId(), localDetection);
                    } catch (IOException e) {
                        logger.error("Failed to reconstruct detection", e);
                    }
                } else {
                    logger.debug("Got partial target detection image");
                }
            }
        }
    }
}
