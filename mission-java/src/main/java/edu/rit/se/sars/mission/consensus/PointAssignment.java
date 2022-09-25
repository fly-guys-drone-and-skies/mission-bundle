package edu.rit.se.sars.mission.consensus;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.proto.external.SwarmMessage;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.domain.GeoLocation2D;
import lombok.Data;

import java.util.UUID;

/**
 * Point assignment for swarm distribution
 */
@Data
public class PointAssignment implements ProtobufSerializable<SwarmMessage.CoverageStateMessage.PointAssignment> {

    private final UUID droneUUID;
    private final int pointId;
    private final PointTask pointTask;

    /**
     * @param droneUUID UUID of drone assigned to point
     * @param pointId Global ID of point
     * @param pointTask Point task
     */
    public PointAssignment(UUID droneUUID, int pointId, PointTask pointTask) {
        this.droneUUID = droneUUID;
        this.pointId = pointId;
        this.pointTask = pointTask;
    }

    public PointAssignment(SwarmMessage.CoverageStateMessage.PointAssignment pointAssignment) {
        this(
            UUIDSerDe.deserialize(pointAssignment.getDroneUUID()),
            pointAssignment.getPointID(),
            new PointTask(pointAssignment.getPointTask())
        );
    }

    @Override
    public SwarmMessage.CoverageStateMessage.PointAssignment toProtobuf() {
        return SwarmMessage.CoverageStateMessage.PointAssignment.newBuilder()
            .setDroneUUID(
                ByteString.copyFrom(
                    UUIDSerDe.serialize(this.droneUUID)
                )
            )
            .setPointID(this.pointId)
            .setPointTask(this.pointTask.toProtobuf())
            .build();
    }
}
