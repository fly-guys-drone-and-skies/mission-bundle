package edu.rit.se.sars.communication.network.protocol;

import edu.rit.se.sars.communication.network.delivery.OutboundMessage;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Protocol implementation for sending and receiving swarm messages
 * @param <M> Message type
 * @param <N> Network entity type
 */
public abstract class ProtocolStrategy<M, N extends NetworkEntity> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolStrategy.class);

    private final BlockingQueue<M> inboundQueue;

    /**
     * @param inboundQueue Queue to push received messages to
     */
    public ProtocolStrategy(BlockingQueue<M> inboundQueue) {
        this.inboundQueue = inboundQueue;
    }

    /**
     * Send outbound message to peer
     * @param outboundMessage Message to send
     */
    public void send(OutboundMessage<M, N> outboundMessage) throws IOException {
        logger.trace("Sending {} to {}", outboundMessage.getMessage(), outboundMessage.getRecipient());

        this.send(outboundMessage.getMessage(), outboundMessage.getRecipient());
    }

    /**
     * Send outbound message to peer over implemented protocol
     * @param message Message to send
     * @param recipient Message recipient
     */
    protected abstract void send(M message, N recipient) throws IOException;

    /**
     * Continuously listen for inbound messages and push them to inbound queue
     */
    public abstract void listen() throws IOException;

    /**
     * @return True if actively listening for inbound messages, false otherwise
     */
    public abstract boolean isListening();

    /**
     * Push received message to inbound queue
     * @param message Message to push to queue
     */
    protected void handleInbound(M message) {
        logger.trace("Got inbound: {}", message);

        this.inboundQueue.add(message);
    }

    @Override
    public void run() {
        logger.info("Starting");

        try {
            this.listen();
        } catch (IOException e) {
            logger.error("Listening failed", e);
        }
    }
}
