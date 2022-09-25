package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.proto.ipc.event.Event;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * IPC drone command completed event
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class CommandCompletedEvent extends DroneEvent {

    public CommandCompletedEvent() {
        super();
    }

    public CommandCompletedEvent(Event event) {
        super(event);
    }

    @Override
    public Event toProtobuf() {
        return this.getProtobufBuilder()
            .setCommandCompleted(
                Event.CommandCompletedEvent.getDefaultInstance()
            ).build();
    }
}
