package edu.rit.se.sars.communication.serde;

import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * UUID serialization and deserialization utility methods
 */
public class UUIDSerDe {

    /**
     * Serialize UUID to bytes
     * @param uuid UUID to serialize
     * @return Bytes representation of UUID
     */
    public static byte[] serialize(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return byteBuffer.array();
    }

    /**
     * Deserialize UUID from bytes
     * @param bytes Bytes representation of UUID
     * @return Deserialized UUID
     */
    public static UUID deserialize(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();

        return new UUID(high, low);
    }

    /**
     * Deserialize UUID from ByteString (protobuf type)
     * @param byteString ByteString representation of UUID
     * @return Deserialized UUID
     */
    public static UUID deserialize(ByteString byteString) {
        return deserialize(byteString.toByteArray());
    }
}
