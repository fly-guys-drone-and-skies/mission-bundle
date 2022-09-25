package edu.rit.se.sars.mission.consensus;

import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.swarm.PingMessage;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelope;
import edu.rit.se.sars.communication.network.delivery.SwarmDeliveryHandler;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Swarm status monitor, responsible for detecting when other drones go down
 * @param <N>
 */
public class SwarmStatusMonitor<N extends NetworkEntity> extends Observable implements MessageHandler<SwarmMessageEnvelope>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SwarmStatusMonitor.class);

    private static final long TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5);
    private static final long INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(15);

    private final SwarmDeliveryHandler<N> deliveryHandler;
    private final Map<UUID, CountDownLatch> pendingPings = new HashMap<>();
    private final Set<UUID> respondingEntities = new HashSet<>();

    private Set<N> entities = new HashSet<>();

    public SwarmStatusMonitor(SwarmDeliveryHandler<N> deliveryHandler) {
        this.deliveryHandler = deliveryHandler;
    }

    public void setEntities(Set<N> entities) {
        this.entities = entities;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Set<N> removeEntities = new HashSet<>();

                for (N entity : entities) {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    this.pendingPings.put(entity.getUuid(), countDownLatch);

                    deliveryHandler.sendMessage(new PingMessage(), entity);

                    countDownLatch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                    if (!this.respondingEntities.remove(entity.getUuid())) {
                        logger.info("No response received from {}", entity.getUuid());

                        removeEntities.add(entity);

                        this.setChanged();
                        this.notifyObservers(entity);
                    }
                }

                this.entities.removeAll(removeEntities);

                Thread.sleep(INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void handleMessage(SwarmMessageEnvelope message) {
        N fromEntity = (N) message.getFromEntity();

        if (this.pendingPings.containsKey(fromEntity.getUuid())) {
            this.respondingEntities.add(fromEntity.getUuid());

            this.pendingPings.remove(fromEntity.getUuid()).countDown();
        }
    }
}
