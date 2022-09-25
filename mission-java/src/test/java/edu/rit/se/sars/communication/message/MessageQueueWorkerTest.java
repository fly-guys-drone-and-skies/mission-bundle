package edu.rit.se.sars.communication.message;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class MessageQueueWorkerTest {

    @Test
    public void testMessageQueueWorkerPublish() throws Exception {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        List<String> sentMessages = Arrays.asList("message1", "message2", "message3");
        queue.add(sentMessages.get(0));

        MessageQueueWorker<String> queueWorker = new MessageQueueWorker<>(queue);
        MessagePublisherTest.TestMessageHandler handler = new MessagePublisherTest.TestMessageHandler();
        queueWorker.addSubscriber(handler);

        Thread queueThread = new Thread(queueWorker);
        queueThread.start();

        queue.add(sentMessages.get(1));
        queue.add(sentMessages.get(2));

        Thread.sleep(10);

        queueThread.interrupt();

        assertEquals(sentMessages, handler.messages);
    }
}
