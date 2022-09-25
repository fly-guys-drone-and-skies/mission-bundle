package edu.rit.se.sars.communication.message.swarm;

import lombok.ToString;

/**
 * Get location of entity
 */
@ToString
public class LocationQueryMessage extends SwarmMessage {

    public LocationQueryMessage() {
        super();
    }

    public LocationQueryMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this();
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setLocationQuery(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationQueryMessage.getDefaultInstance()
            ).build();
    }
}
