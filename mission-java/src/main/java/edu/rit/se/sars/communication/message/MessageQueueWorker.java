package edu.rit.se.sars.communication.message;

import java.util.concurrent.BlockingQueue;

/**
 * Queue worker that will continuously read from queue and publish messages to handlers
 * @param <T> Message type
 */
public class MessageQueueWorker<T> extends MessagePublisher<T> implements Runnable {

    private final BlockingQueue<T> messageQueue;

    public MessageQueueWorker(BlockingQueue<T> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.publish(this.messageQueue.take()); // will block until message is available in queue
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

