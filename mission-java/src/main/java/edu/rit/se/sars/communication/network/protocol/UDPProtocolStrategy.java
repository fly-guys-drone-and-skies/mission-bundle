package edu.rit.se.sars.communication.network.protocol;

import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.serde.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * UDP protocol implementation
 * @param <M> Message type
 */
public class UDPProtocolStrategy<M> extends ProtocolStrategy<M, IPEntity> {

    private static final Logger logger = LoggerFactory.getLogger(UDPProtocolStrategy.class);

    private final MessageSerializer<M> serializer;
    private final MessageDeserializer<M> deserializer;
    private final InetSocketAddress bindAddress;

    private DatagramSocket socket;

    /**
     * @param inboundQueue Queue to push received messages to
     * @param serializer Outbound message serializer
     * @param deserializer Inbound message deserializer
     * @param bindAddress Address for server to listen on
     */
    public UDPProtocolStrategy(
        BlockingQueue<M> inboundQueue,
        MessageSerializer<M> serializer,
        MessageDeserializer<M> deserializer,
        InetSocketAddress bindAddress
    ) {
        super(inboundQueue);

        this.deserializer = deserializer;
        this.serializer = serializer;
        this.bindAddress = bindAddress;
    }

    @Override
    protected void send(M message, IPEntity recipient) throws IOException {
        if (this.socket != null) {
            byte[] wire = this.serializer.serialize(message);

            this.socket.send(
                new DatagramPacket(
                    wire,
                    wire.length,
                    recipient.getAddress()
                )
            );
        } else {
            throw new IOException("Socket not initialized");
        }
    }

    @Override
    public void listen() throws IOException {
        logger.debug("Binding to {}", this.bindAddress);

        this.socket = new DatagramSocket(bindAddress);

        byte[] recvBuffer = new byte[65535];
        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            socket.receive(receivePacket);

            // Extract message from buffer
            byte[] wire = Arrays.copyOfRange(
                receivePacket.getData(),
                receivePacket.getOffset(),
            receivePacket.getOffset() + receivePacket.getLength()
            );

            M message = this.deserializer.deserialize(wire);
            this.handleInbound(message);
        }

        this.socket = null;
    }

    @Override
    public boolean isListening() {
        return this.socket != null;
    }
}
