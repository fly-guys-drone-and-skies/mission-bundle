package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.domain.GeoLocation3D;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Announce location of sending entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocationStatusMessage extends SwarmMessage {

    private final GeoLocation3D location;

    /**
     * @param location Entity location
     */
    public LocationStatusMessage(GeoLocation3D location) {
        super();

        this.location = location;
    }

    public LocationStatusMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            new GeoLocation3D(
                message.getLocationStatus().getLocation()
            )
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setLocationStatus(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.LocationStatusMessage.newBuilder()
                    .setLocation(this.location.toProtobuf())
                    .build()
            ).build();
    }
}
