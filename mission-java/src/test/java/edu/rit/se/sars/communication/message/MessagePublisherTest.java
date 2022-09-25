package edu.rit.se.sars.communication.message;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessagePublisherTest {

    @Test
    public void testMessagePublisherPublish() {
        MessagePublisher<String> publisher = new MessagePublisher<String>() {};

        List<TestMessageHandler> handlers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TestMessageHandler handler = new TestMessageHandler();

            handlers.add(handler);
            publisher.addSubscriber(handler);
        }

        String message = "test";
        publisher.publish(message);

        for (TestMessageHandler handler : handlers) {
            assertEquals(handler.messages.size(), 1);
            assertEquals(handler.messages.get(0), message);
        }
    }

    protected static class TestMessageHandler implements MessageHandler<String> {

        public final List<String> messages = new ArrayList<>();

        @Override
        public void handleMessage(String message) {
            this.messages.add(message);
        }

        @Override
        public boolean isBlocking() {
            return true;
        }
    }
}
