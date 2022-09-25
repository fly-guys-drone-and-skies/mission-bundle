package edu.rit.se.sars.mission.consensus;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.proto.external.SwarmMessage;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Container for keeping track of point and altitude assignments
 */
@Data
@AllArgsConstructor
public class CoverageState implements ProtobufSerializable<SwarmMessage.CoverageStateMessage> {

    private final int version;
    private final List<PointAssignment> remainingPointAssignments;
    private final Map<UUID, Double> altitudeAssignments;

    public CoverageState(SwarmMessage.CoverageStateMessage message) {
        this(
            message.getVersion(),
            message.getRemainingPointAssignmentsList()
                .stream()
                .map(PointAssignment::new)
                .collect(Collectors.toList()),
            message.getAltitudeAssignmentsList()
                .stream()
                .collect(
                    Collectors.toMap(
                        assignment -> UUIDSerDe.deserialize(assignment.getDroneUUID()),
                        SwarmMessage.CoverageStateMessage.AltitudeAssignment::getAltitudeMeters
                    )
                )
        );
    }

    public Set<UUID> getDroneUUIDs() {
        return this.getAltitudeAssignments().keySet();
    }

    @Override
    public SwarmMessage.CoverageStateMessage toProtobuf() {
        return SwarmMessage.CoverageStateMessage.newBuilder()
            .setVersion(this.version)
            .addAllRemainingPointAssignments(
                this.remainingPointAssignments
                    .stream()
                    .map(PointAssignment::toProtobuf)
                    .collect(Collectors.toList())
            )
            .addAllAltitudeAssignments(
                this.altitudeAssignments.entrySet()
                    .stream()
                    .map( entry ->
                        SwarmMessage.CoverageStateMessage.AltitudeAssignment.newBuilder()
                            .setDroneUUID(
                                ByteString.copyFrom(UUIDSerDe.serialize(entry.getKey()))
                            )
                            .setAltitudeMeters(entry.getValue())
                            .build()
                    )
                    .collect(Collectors.toList())
            ).build();
    }
}
