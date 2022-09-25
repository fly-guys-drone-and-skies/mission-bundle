package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.proto.ipc.event.Event;
import edu.rit.se.sars.domain.Orientation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Drone orientation (absolute) change event
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class OrientationStatusEvent extends DroneEvent {

    private final Orientation orientation;

    /**
     * @param orientation Absolute orientation of drone (relative to north)
     */
    public OrientationStatusEvent(Orientation orientation) {
        super();

        this.orientation = orientation;
    }

    public OrientationStatusEvent(Event event) {
        super(event);

        this.orientation = new Orientation(
            event.getOrientationStatus().getOrientation()
        );
    }

    @Override
    public Event toProtobuf() {
        return this.getProtobufBuilder()
            .setOrientationStatus(
                Event.OrientationStatusEvent.newBuilder()
                    .setOrientation(this.orientation.toProtobuf())
                    .build()
            ).build();
    }
}