package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.communication.proto.ipc.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Position change event
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class PositionStatusEvent extends DroneEvent {

    private final GeoLocation3D location;

    /**
     * @param location Current drone position
     */
    public PositionStatusEvent(GeoLocation3D location) {
        super();

        this.location = location;
    }

    public PositionStatusEvent(Event event) {
        super(event);

        this.location = new GeoLocation3D(
            event.getPositionStatus().getLocation()
        );
    }

    @Override
    public Event toProtobuf() {
        return this.getProtobufBuilder()
            .setPositionStatus(
                Event.PositionStatusEvent.newBuilder()
                    .setLocation(this.location.toProtobuf())
                    .build()
            ).build();
    }
}
