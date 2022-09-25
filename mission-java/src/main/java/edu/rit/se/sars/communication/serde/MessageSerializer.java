package edu.rit.se.sars.communication.serde;

import java.io.IOException;

/**
 * Serializer for specified message type
 * @param <T> Message type to serialize
 */
public interface MessageSerializer<T> {
    /**
     * Serialize message object to bytes
     * @param object Message object to serialize
     * @return Representation of message object in bytes
     */
    byte[] serialize(T object) throws IOException;
}
