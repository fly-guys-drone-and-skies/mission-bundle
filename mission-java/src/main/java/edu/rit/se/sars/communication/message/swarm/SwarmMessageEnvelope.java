package edu.rit.se.sars.communication.message.swarm;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

/**
 * Envelope to wrap swarm messages
 */
@Data
public class SwarmMessageEnvelope implements ProtobufSerializable<edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope> {

    private final UUID uuid;
    private final NetworkEntity fromEntity;
    private final Optional<NetworkEntity> toEntity;
    private final SwarmMessage message;

    /**
     * @param uuid Message UUID
     * @param fromEntity Entity sent from
     * @param toEntity Entity sent to, none if entity information not known (broadcast)
     * @param message Swarm message
     */
    public SwarmMessageEnvelope(UUID uuid, NetworkEntity fromEntity, Optional<NetworkEntity> toEntity, SwarmMessage message) {
        this.uuid = uuid;
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
        this.message = message;
    }

    /**
     * @param fromEntity Sending entity
     * @param toEntity Target entity
     * @param message Swarm message
     */
    public SwarmMessageEnvelope(NetworkEntity fromEntity, NetworkEntity toEntity, SwarmMessage message) {
        this(UUID.randomUUID(), fromEntity, Optional.of(toEntity), message);
    }

    /**
     * Construct envelope with no target entity (broadcast usage)
     * @param fromEntity Sending entity
     * @param message Swarm message
     */
    public SwarmMessageEnvelope(NetworkEntity fromEntity, SwarmMessage message) {
        this(UUID.randomUUID(), fromEntity, Optional.empty(), message);
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope toProtobuf() {
        edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope.Builder builder =
            edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope.newBuilder()
            .setUuid(
                ByteString.copyFrom(
                    UUIDSerDe.serialize(this.uuid)
                )
            )
            .setFromEntity(this.fromEntity.toProtobuf())
            .setMessage(this.message.toProtobuf());

        this.toEntity.ifPresent(networkEntity -> builder.setToEntity(networkEntity.toProtobuf()));

        return builder.build();
    }

    /**
     * Convert protobuf type to object type
     * @param envelope Protobuf envelope instance
     * @return Envelope constructed from protobuf type
     */
    public static SwarmMessageEnvelope fromProtobuf(edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope envelope) {
        UUID uuid = UUIDSerDe.deserialize(envelope.getUuid());
        NetworkEntity fromEntity = NetworkEntity.fromProtobuf(envelope.getFromEntity());
        Optional<NetworkEntity> toEntity = Optional.empty();
        if (envelope.hasToEntity()) {
            toEntity = Optional.of(NetworkEntity.fromProtobuf(envelope.getToEntity()));
        }
        SwarmMessage message = SwarmMessage.fromProtobuf(envelope.getMessage());

        return new SwarmMessageEnvelope(uuid, fromEntity, toEntity, message);
    }
}
