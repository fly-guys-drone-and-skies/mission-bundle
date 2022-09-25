package edu.rit.se.sars.communication.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Local message publisher. Handlers subscribe to a publisher, then receive all messages it receives.
 * @param <T> Message type
 */
public abstract class MessagePublisher<T> {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    private static final int NUM_THREADS = 5;

    private final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    private final List<MessageHandler<T>> handlers;

    public MessagePublisher() {
        this.handlers = Collections.synchronizedList(new LinkedList<>());
    }

    /**
     * Subscribe handler to all messages
     * @param handler Handler to publish messages to
     */
    public void addSubscriber(MessageHandler<T> handler) {
        this.handlers.add(handler);
    }

    /**
     * Publish message to all handlers
     * @param message Message to publish
     */
    public void publish(T message) {
        this.handlers.forEach(handler -> {
            Future<?> future = executor.submit(() -> {
                try {
                    handler.handleMessage(message);
                } catch (Exception e) {
                    logger.error("Handler failed to process message", e);
                }
            });
            if (handler.isBlocking()) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    logger.error("Interrupted", e);
                } catch (ExecutionException e) {
                    logger.error("Failed to process message", e);
                }
            }
        });
    }
}
