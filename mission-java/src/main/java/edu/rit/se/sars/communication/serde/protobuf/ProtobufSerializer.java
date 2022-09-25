package edu.rit.se.sars.communication.serde.protobuf;

import edu.rit.se.sars.communication.serde.MessageSerializer;

/**
 * Serializer for objects to byte representation of their associated protobuf types
 * @param <T> Protobuf object type
 */
public class ProtobufSerializer<T extends ProtobufSerializable<?>> implements MessageSerializer<T> {
    @Override
    public byte[] serialize(T object) {
        return object.toProtobuf().toByteArray();
    }
}
