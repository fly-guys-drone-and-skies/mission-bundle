package edu.rit.se.sars.communication.ipc;

import edu.rit.se.sars.communication.message.MessagePublisher;
import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.serde.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class ProtoQueuedClient<S,D> implements Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(ProtoQueuedClient.class);

    protected final InetSocketAddress serverAddress;
    public final MessageSerializer<S> serializer;
    public final MessageDeserializer<D> deserializer;
    public final MessagePublisher<D> publisher;

    public ProtoQueuedClient (
            InetSocketAddress serverAddress,
            MessageSerializer<S> serializer,
            MessageDeserializer<D> deserializer,
            MessagePublisher<D> publisher
    ) {
        this.serverAddress = serverAddress;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.publisher = publisher;
    }

    public abstract void socketSend(byte[] message);

    public abstract void socketConnect(InetSocketAddress addr);

    public abstract byte[] blockingReceive();



    public void send(S message) throws IOException {
        try {
            byte[] msg = serializer.serialize(message);
            socketSend(serializer.serialize(message));
        } catch (IOException e) {
            logger.debug("Failed to send message");
            throw e;
        }
    }

    public void sendRaw (byte [] bytes) {
        socketSend(bytes);
    }



    @Override
    public void run() {
        logger.debug("Connecting to server: {}", this.serverAddress);
        socketConnect(serverAddress);
        logger.debug("Connected!");
        while (!Thread.currentThread().isInterrupted()) {
            byte[] messageData = blockingReceive();

            try {
                D message = this.deserializer.deserialize(messageData);
                logger.debug("Received message: {}", message);
                this.publisher.publish(message);
            } catch (IOException e) {
                logger.error("Failed to deserialize message", e);
            }
        }
    }
}
