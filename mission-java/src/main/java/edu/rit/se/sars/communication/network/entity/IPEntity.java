package edu.rit.se.sars.communication.network.entity;

import edu.rit.se.sars.communication.serde.UUIDSerDe;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * IP-based network entity (TCP/UDP)
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class IPEntity extends NetworkEntity {

    private final InetSocketAddress address;

    /**
     * @param type Entity type
     * @param uuid Entity UUID
     * @param address Server address to reach entity
     */
    public IPEntity(final NetworkEntityType type, final UUID uuid, final InetSocketAddress address) {
        super(type, uuid);

        this.address = address;
    }

    /**
     * @param type Entity type
     * @param address Server address to reach entity
     */
    public IPEntity(final NetworkEntityType type, final InetSocketAddress address) {
        this(type, UUID.randomUUID(), address);
    }

    /**
     * @param message Protobuf instance of NetworkEntity
     */
    public IPEntity(final edu.rit.se.sars.communication.proto.external.NetworkEntity message) {
        this(
            NetworkEntityType.valueOf(message.getType().name()),
            UUIDSerDe.deserialize(message.getUuid()),
            new InetSocketAddress(
                message.getIpEntity().getHost(),
                message.getIpEntity().getPort()
            )
        );
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.NetworkEntity toProtobuf() {
        return this.getProtobufBuilder()
            .setIpEntity(
                edu.rit.se.sars.communication.proto.external.NetworkEntity.IPEntity.newBuilder()
                    .setHost(this.address.getHostString())
                    .setPort(this.address.getPort())
                    .build()
            ).build();
    }
}
