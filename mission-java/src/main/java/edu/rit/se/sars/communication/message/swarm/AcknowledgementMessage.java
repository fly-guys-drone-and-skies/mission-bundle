package edu.rit.se.sars.communication.message.swarm;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Acknowledgment of swarm message
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AcknowledgementMessage extends SwarmMessage {

    private final UUID acknowledgedUUID;

    /**
     * @param acknowledgedUUID UUID of message being acknowledged
     */
    public AcknowledgementMessage(final UUID acknowledgedUUID) {
        super();

        this.acknowledgedUUID = acknowledgedUUID;
    }

    public AcknowledgementMessage(edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            UUIDSerDe.deserialize(message.getAcknowledgement().getAcknowledgedUUID())
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setAcknowledgement(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.AcknowledgementMessage.newBuilder()
                    .setAcknowledgedUUID(
                        ByteString.copyFrom(UUIDSerDe.serialize(this.acknowledgedUUID))
                    ).build()
            ).build();
    }
}
