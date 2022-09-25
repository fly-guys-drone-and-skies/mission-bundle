package edu.rit.se.sars.communication.network.delivery;

import edu.rit.se.sars.communication.message.MessageHandler;
import edu.rit.se.sars.communication.message.swarm.AcknowledgementMessage;
import edu.rit.se.sars.communication.message.swarm.SwarmMessage;
import edu.rit.se.sars.communication.message.swarm.SwarmMessageEnvelope;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityRegistry;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.communication.network.protocol.ProtocolStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Reliable message delivery handler.
 * Messages will be continuously re-sent at the configured interval until an acknowledgment message is received or
 * they are otherwise cancelled.
 * @param <N> Type of swarm network entities
 */
public class SwarmDeliveryHandler<N extends NetworkEntity> implements MessageHandler<SwarmMessageEnvelope>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SwarmDeliveryHandler.class);

    protected static final long RESEND_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(1);

    private final ProtocolStrategy<SwarmMessageEnvelope, N> protocolStrategy;
    private final NetworkEntityRegistry<N> entityRegistry;

    private final BlockingQueue<DelayedObject<OutboundMessage<SwarmMessageEnvelope, N>>> resendQueue;

    /**
     * @param protocolStrategy Protocol for sending messages
     * @param entityRegistry Swarm entity registry
     */
    public SwarmDeliveryHandler(
        ProtocolStrategy<SwarmMessageEnvelope, N> protocolStrategy,
        NetworkEntityRegistry<N> entityRegistry
    ) {
        this.protocolStrategy = protocolStrategy;
        this.entityRegistry = entityRegistry;

        this.resendQueue = new DelayQueue<>();
    }

    /**
     * Send message to all entities in swarm
     * @param message Message to send
     * @return List of outbound messages sent to entities
     */
    public List<OutboundMessage<SwarmMessageEnvelope, ?>> sendMessage(SwarmMessage message) {
        logger.trace("Sending to all entities: {}", message);

        return this.entityRegistry.getOtherEntities().stream().map(
            entity -> this.sendMessage(message, entity)
        ).collect(Collectors.toList());
    }

    /**
     * Send message to all swarm entities of specified type
     * @param message Message to send
     * @param toEntityType Entity type to send message to
     * @return List of outbound messages sent to entities
     */
    public List<OutboundMessage<SwarmMessageEnvelope, ?>> sendMessage(SwarmMessage message, NetworkEntityType toEntityType) {
        logger.trace("Sending {} to entity type {}", message, toEntityType);

        return this.entityRegistry.getOtherEntitiesOfType(toEntityType).stream().map(
            entity -> this.sendMessage(message, entity)
        ).collect(Collectors.toList());
    }

    /**
     * Send message to specified entity
     * @param message Message to send
     * @param toEntity Entity to send message to
     * @return Outbound message sent to entity
     */
    public OutboundMessage<SwarmMessageEnvelope, ?> sendMessage(SwarmMessage message, N toEntity) {
        logger.trace("Sending {} to entity {}", message, toEntity);

        SwarmMessageEnvelope envelope = new SwarmMessageEnvelope(
            this.entityRegistry.getSelf(),
            toEntity,
            message
        );

        OutboundMessage<SwarmMessageEnvelope, N> outboundMessage = new OutboundMessage<>(envelope, toEntity);
        this.sendMessage(outboundMessage);

        return outboundMessage;
    }

    /**
     * Send outbound message and queue resend if applicable
     * @param outboundMessage Outbound message to send
     */
    private void sendMessage(OutboundMessage<SwarmMessageEnvelope, N> outboundMessage) {
        try {
            protocolStrategy.send(outboundMessage);
        } catch (IOException e) {
            logger.error("Failed to send message", e);
        }

        final SwarmMessageEnvelope envelope = outboundMessage.getMessage();
        // Acknowledgment messages shouldn't be queued for resending (otherwise infinite feedback loop)
        if (!(envelope.getMessage() instanceof AcknowledgementMessage)) {
            final DelayedObject<OutboundMessage<SwarmMessageEnvelope, N>> resendMessage =
                    new DelayedObject<>(outboundMessage, RESEND_DELAY_MILLIS);
            resendQueue.add(resendMessage);
        }
    }

    @Override
    public void handleMessage(SwarmMessageEnvelope envelope) {
        this.entityRegistry.addEntity((N) envelope.getFromEntity());

        SwarmMessage message = envelope.getMessage();
        logger.debug("Received: {}", message);

        if (message instanceof AcknowledgementMessage) {
            // Cancel sending message that was acknowledged
            this.cancelResend((AcknowledgementMessage) message);
        } else {
            // Acknowledge receipt of message
            this.sendMessage(
                new AcknowledgementMessage(envelope.getUuid()),
                (N) envelope.getFromEntity()
            );
        }
    }

    @Override
    public boolean isBlocking() {
        // Blocking to ensure entities become registered before other handlers execute
        return true;
    }

    /**
     * Cancel all pending resends to specified entity
     * @param toEntity Entity to cancel resends to
     */
    public void cancelResends(N toEntity) {
        synchronized (resendQueue) {
            this.resendQueue.removeIf(o ->
                o.getObject()
                    .getRecipient().getUuid().equals(toEntity.getUuid())
            );
        }
    }

    /**
     * Cancel resend of message associated with received acknowledgment
     * @param ackMessage Received acknowledgment associated with sent message
     */
    private void cancelResend(AcknowledgementMessage ackMessage) {
        logger.debug("Cancelling resend of {}", ackMessage.getAcknowledgedUUID());

        synchronized (resendQueue) {
            this.resendQueue.removeIf(o ->
                o.getObject()
                    .getMessage()
                    .getUuid()
                    .equals(
                        ackMessage.getAcknowledgedUUID()
                    )
            );
        }
    }

    public NetworkEntityRegistry<N> getEntityRegistry() {
        return this.entityRegistry;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Will block until delay of a message is met
                this.sendMessage(
                    this.resendQueue.take().getObject()
                );
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Object with delay for usage in DelayQueue
     * @param <T> Object type
     */
    private static class DelayedObject<T> implements Delayed {
        private final T object;
        private final long targetTimeMillis;

        protected DelayedObject(T object, long delayMillis) {
            this.object = object;
            this.targetTimeMillis = System.currentTimeMillis() + delayMillis;
        }

        public T getObject() {
            return this.object;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.targetTimeMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}
