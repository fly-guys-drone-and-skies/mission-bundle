package edu.rit.se.sars.communication.network.entity;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.communication.proto.external.NetworkEntity.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Entity in swarm network
 */
@Data
@AllArgsConstructor
public abstract class NetworkEntity implements ProtobufSerializable<edu.rit.se.sars.communication.proto.external.NetworkEntity> {

    private final NetworkEntityType type;
    private final UUID uuid;

    public NetworkEntity(final NetworkEntityType type) {
        this(type, UUID.randomUUID());
    }

    /**
     * Construct entity from protobuf instance
     * @param entity Protobuf instance
     * @return Constructed instance
     */
    public static NetworkEntity fromProtobuf(edu.rit.se.sars.communication.proto.external.NetworkEntity entity) {
        switch (entity.getEntityCase()) {
            case IPENTITY:
                return new IPEntity(entity);
            default:
                throw new UnsupportedOperationException("Unexpected message type");
        }
    }

    protected edu.rit.se.sars.communication.proto.external.NetworkEntity.Builder getProtobufBuilder() {
        return edu.rit.se.sars.communication.proto.external.NetworkEntity.newBuilder()
            .setType(
                EntityType.valueOf(this.type.name())
            )
            .setUuid(
                ByteString.copyFrom(
                    UUIDSerDe.serialize(this.uuid)
                )
            );
    }
}
