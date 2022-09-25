package edu.rit.se.sars.communication.serde.protobuf;

import com.google.protobuf.GeneratedMessageV3;

/**
 * Protobuf serialization
 * @param <T> Protobuf message type
 */
public interface ProtobufSerializable<T extends GeneratedMessageV3> {
    T toProtobuf();
}
