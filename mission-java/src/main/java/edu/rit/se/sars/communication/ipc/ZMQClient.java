package edu.rit.se.sars.communication.ipc;

import edu.rit.se.sars.communication.message.MessagePublisher;
import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.serde.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.net.InetSocketAddress;

import static org.zeromq.ZMQ.NOBLOCK;

/**
 * ZeroMQ PAIR client
 * https://zeromq.org/socket-api/#exclusive-pair-pattern
 * @param <S> Outbound message type (for serialization)
 * @param <D> Inbound message type (for deserialization)
 */
public class ZMQClient<S, D> extends ProtoQueuedClient<S,D> {

    private static final Logger logger = LoggerFactory.getLogger(ZMQClient.class);

    private static final String connectStr = "tcp://%s:%d";

    private final ZMQ.Socket socket;

    /**
     * @param serverAddress ZeroMQ PAIR server to connect to
     * @param serializer Serializer for outbound messages
     * @param deserializer Deserialize for inbound messages
     * @param publisher Publisher for handling inbound messages
     */
    public ZMQClient(
        InetSocketAddress serverAddress,
        MessageSerializer<S> serializer,
        MessageDeserializer<D> deserializer,
        MessagePublisher<D> publisher
    ) {
        super(serverAddress,serializer,deserializer,publisher);
        this.socket = new ZContext().createSocket(SocketType.PAIR); // strictly one-to-one
    }

    @Override
    public void socketSend(byte[] message) {
        this.socket.send(message, NOBLOCK);
    }

    @Override
    public void socketConnect(InetSocketAddress addr) {
        this.socket.connect(String.format(connectStr, addr.getHostString(), addr.getPort()));
    }

    @Override
    public byte[] blockingReceive() {
        return this.socket.recv(0);
    }
}
