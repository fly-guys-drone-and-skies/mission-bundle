package edu.rit.se.sars.communication.message.swarm;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.mission.consensus.PointTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Candidate for focusing - inform drones of task that they will possibly need to distribute in the future
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FocusDetectionCandidateMessage extends SwarmMessage {

    private final UUID detectionUUID;
    private final PointTask pointTask;

    public FocusDetectionCandidateMessage(UUID detectionUUID, PointTask pointTask) {
        super();

        this.detectionUUID = detectionUUID;
        this.pointTask = pointTask;
    }

    public FocusDetectionCandidateMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            UUIDSerDe.deserialize(
                message.getFocusDetectionCandidate().getDetectionUUID()
            ),
            new PointTask(
                message.getFocusDetectionCandidate().getFocusPointTask()
            )
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setFocusDetectionCandidate(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionCandidateMessage.newBuilder()
                    .setDetectionUUID(
                        ByteString.copyFrom(UUIDSerDe.serialize(this.detectionUUID))
                    )
                    .setFocusPointTask(this.pointTask.toProtobuf())
                    .build()
            ).build();
    }
}
