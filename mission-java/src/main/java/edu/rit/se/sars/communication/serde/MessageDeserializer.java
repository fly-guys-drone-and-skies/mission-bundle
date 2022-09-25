package edu.rit.se.sars.communication.serde;

import java.io.IOException;

/**
 * Deserializer for specified message type
 * @param <T> Message type to deserialize to
 */
public interface MessageDeserializer<T> {
    /**
     * Deserialize bytes to message object
     * @param messageData Message representation in bytes to deserialize
     * @return Deserialized object
     */
    T deserialize(byte[] messageData) throws IOException;
}
