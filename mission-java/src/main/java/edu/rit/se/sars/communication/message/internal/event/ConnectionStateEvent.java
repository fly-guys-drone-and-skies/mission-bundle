package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.proto.ipc.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Drone SDK connection state change event
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectionStateEvent extends DroneEvent {

    private final boolean isConnected;

    /**
     * @param isConnected SDK connection state. True if currently connected to drone, false otherwise.
     */
    public ConnectionStateEvent(boolean isConnected) {
        super();

        this.isConnected = isConnected;
    }

    public ConnectionStateEvent(Event event) {
        super(event);

        this.isConnected = event.getConnectionState().getIsConnected();
    }

    @Override
    public Event toProtobuf() {
        return this.getProtobufBuilder()
            .setConnectionState(
                Event.ConnectionStateEvent.newBuilder()
                    .setIsConnected(this.isConnected)
                    .build()
            ).build();
    }
}
