package edu.rit.se.sars.communication.message.swarm;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Instruct entity to return home
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class ReturnHomeMessage extends SwarmMessage {

    public ReturnHomeMessage() {
        super();
    }

    public ReturnHomeMessage(edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this();
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setReturnHome(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.ReturnHomeMessage.newBuilder().build()
            ).build();
    }
}
