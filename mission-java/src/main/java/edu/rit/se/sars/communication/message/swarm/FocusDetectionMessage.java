package edu.rit.se.sars.communication.message.swarm;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Instruct drones to act on a candidate detection focus task (distributed via FocusDetectionCandidateMessage)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FocusDetectionMessage extends SwarmMessage {

    private final UUID detectionUUID;

    public FocusDetectionMessage(UUID detectionUUID) {
        super();

        this.detectionUUID = detectionUUID;
    }

    public FocusDetectionMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            UUIDSerDe.deserialize(
                message.getFocusDetection().getDetectionUUID()
            )
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setFocusDetection(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.FocusDetectionMessage.newBuilder()
                    .setDetectionUUID(
                        ByteString.copyFrom(UUIDSerDe.serialize(this.detectionUUID))
                    )
                    .build()
            ).build();
    }
}
