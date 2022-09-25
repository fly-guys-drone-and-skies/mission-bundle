package edu.rit.se.sars.communication.network.protocol;

import edu.rit.se.sars.communication.network.delivery.OutboundMessage;
import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.serde.MessageSerializer;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class UDPProtocolStrategyTest {

    private static final long timeoutMillis = 10;

    @Test
    public void testSendReceiveConsecutive() throws Exception {
        final BlockingQueue<String> queue = new PriorityBlockingQueue<>();

        final InetSocketAddress addr1 = new InetSocketAddress("127.0.0.1", 12345);
        final InetSocketAddress addr2 = new InetSocketAddress("127.0.0.1", 12346);

        ProtocolStrategy<String, IPEntity> client1 = new UDPProtocolStrategy<>(queue, messageSerializer, messageDeserializer, addr1);
        ProtocolStrategy<String, IPEntity> client2 = new UDPProtocolStrategy<>(queue, messageSerializer, messageDeserializer, addr2);

        ProtocolStrategy<?, ?>[] clients = new ProtocolStrategy<?, ?>[] { client1, client2 };

        List<Thread> threads = Arrays.stream(clients).map(client -> {
            Thread thread = new Thread(client);
            thread.start();

            while (!client1.isListening()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

            return thread;
        }).collect(Collectors.toList());


        String message1 = "message1";
        String message2 = "message2";

        client1.send(new OutboundMessage<>(message1, new IPEntity(NetworkEntityType.DRONE, addr2)));
        assertEquals(queue.poll(timeoutMillis, TimeUnit.MILLISECONDS), message1);

        client1.send(new OutboundMessage<>(message2, new IPEntity(NetworkEntityType.DRONE, addr2)));
        assertEquals(queue.poll(timeoutMillis, TimeUnit.MILLISECONDS), message2);

        threads.forEach(Thread::interrupt);
    }

    public static final MessageSerializer<String> messageSerializer = String::getBytes;
    public static final MessageDeserializer<String> messageDeserializer = String::new;
}
