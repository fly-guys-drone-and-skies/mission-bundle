package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.serde.MessageDeserializer;
import edu.rit.se.sars.communication.proto.ipc.event.Event;

import java.io.IOException;

/**
 * Deserializer for protobuf bytes representation of drone events
 */
public class DroneEventDeserializer implements MessageDeserializer<DroneEvent> {

    @Override
    public DroneEvent deserialize(byte[] messageData) throws IOException {
        Event event = Event.parseFrom(messageData);

        switch (event.getEventCase()) {
            case COMMANDCOMPLETED:
                return new CommandCompletedEvent(event);
            case BATTERYSTATUS:
                return new BatteryStatusEvent(event);
            case POSITIONSTATUS:
                return new PositionStatusEvent(event);
            case ORIENTATIONSTATUS:
                return new OrientationStatusEvent(event);
            case CONNECTIONSTATE:
                return new ConnectionStateEvent(event);
            default:
                throw new UnsupportedOperationException("Unexpected event type");
        }
    }
}
