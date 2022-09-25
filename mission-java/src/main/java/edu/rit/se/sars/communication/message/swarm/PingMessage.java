package edu.rit.se.sars.communication.message.swarm;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Ping message, used to check if entity is reachable based on ACK
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class PingMessage extends SwarmMessage {

    public PingMessage() {
        super();
    }

    public PingMessage(edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this();
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setPing(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.PingMessage.newBuilder().build()
            ).build();
    }
}
