package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.proto.ipc.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Drone battery status
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BatteryStatusEvent extends DroneEvent {

    private final float percentageRemaining;

    /**
     * @param percentageRemaining Percentage of battery remaining
     */
    public BatteryStatusEvent(float percentageRemaining) {
        super();

        this.percentageRemaining = percentageRemaining;
    }

    public BatteryStatusEvent(Event event) {
        super(event);

        this.percentageRemaining = event.getBatteryStatus().getPercentageRemaining();
    }

    @Override
    public Event toProtobuf() {
        return this.getProtobufBuilder()
            .setBatteryStatus(
                Event.BatteryStatusEvent.newBuilder()
                    .setPercentageRemaining(this.percentageRemaining)
                    .build()
            ).build();
    }
}
