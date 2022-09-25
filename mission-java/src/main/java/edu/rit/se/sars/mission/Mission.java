package edu.rit.se.sars.mission;

import edu.rit.se.sars.communication.proto.external.SwarmMessage;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.domain.GeoPolygon;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.mission.target.detection.TargetType;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Data
public class Mission implements ProtobufSerializable<SwarmMessage.MissionDefinitionMessage> {

    private final Map<UUID, ? extends NetworkEntity> networkEntities;
    private final GeoPolygon area;
    private final double minAltitudeMeters;
    private final double altitudeSeparationMeters;
    private final TargetType targetType;

    /**
     * @param networkEntities Entities to use for mission, including operator and drones
     * @param area Search area
     * @param minAltitudeMeters Minimum operating altitude for drones (m)
     * @param altitudeSeparationMeters Altitude separation for drones for vertical deconfliction (m)
     * @param targetType Type of target to search for
     */
    public Mission(
        Map<UUID, ? extends NetworkEntity> networkEntities,
        GeoPolygon area,
        double minAltitudeMeters,
        double altitudeSeparationMeters,
        TargetType targetType
    ) {
        this.networkEntities = networkEntities;
        this.area = area;
        this.minAltitudeMeters = minAltitudeMeters;
        this.altitudeSeparationMeters = altitudeSeparationMeters;
        this.targetType = targetType;
    }

    public Mission(SwarmMessage.MissionDefinitionMessage message) {
        this(
            message.getNetworkEntitiesList().stream().collect(
                Collectors.toMap(
                    entity -> UUIDSerDe.deserialize(entity.getUuid()),
                    NetworkEntity::fromProtobuf
                )
            ),
            new GeoPolygon(message.getSearchArea()),
            message.getMinAltitudeMeters(),
            message.getAltitudeSeparationMeters(),
            TargetType.valueOf(message.getTargetType().name())
        );
    }

    public Set<NetworkEntity> getNetworkEntities() {
        return new HashSet<>(this.networkEntities.values());
    }

    @Override
    public SwarmMessage.MissionDefinitionMessage toProtobuf() {
        return SwarmMessage.MissionDefinitionMessage.newBuilder()
            .addAllNetworkEntities(
                this.networkEntities.values().stream()
                    .map(NetworkEntity::toProtobuf)
                    .collect(Collectors.toList())
            )
            .setSearchArea(this.area.toProtobuf())
            .setMinAltitudeMeters(this.minAltitudeMeters)
            .setAltitudeSeparationMeters(this.altitudeSeparationMeters)
            .setTargetType(
                edu.rit.se.sars.communication.proto.external.TargetType.valueOf(this.targetType.name())
            ).build();
    }
}
