package edu.rit.se.sars.communication.message;

public interface MessageHandler<T> {
    /**
     * Implementation of message effects
     * @param message Message to handle
     */
    void handleMessage(T message);

    /**
     * Determine whether messages should be handled synchronously w.r.t. other handlers
     * This is to be used where the side-effects of one handler must occur before another
     * @return True if messages should should be handled synchronously, false otherwise
     */
    default boolean isBlocking() {
        return false;
    }
}
