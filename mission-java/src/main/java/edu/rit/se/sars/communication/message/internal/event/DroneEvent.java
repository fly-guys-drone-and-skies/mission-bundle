package edu.rit.se.sars.communication.message.internal.event;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.proto.ipc.event.Event;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

/**
 * IPC drone event
 */
@Data
public abstract class DroneEvent implements ProtobufSerializable<Event> {

    private final Optional<UUID> commandUUID;

    public DroneEvent() {
        this.commandUUID = Optional.empty();
    }

    /**
     * @param commandUUID UUID of command that this event is in response to
     */
    public DroneEvent(UUID commandUUID) {
        this.commandUUID = Optional.of(commandUUID);
    }

    public DroneEvent(Event event) {
        if (event.hasCommandUUID()) {
            this.commandUUID = Optional.of(
                UUIDSerDe.deserialize(event.getCommandUUID())
            );
        } else {
            this.commandUUID = Optional.empty();
        }
    }

    /**
     * @return Base protobuf builder to be used for all events
     */
    protected Event.Builder getProtobufBuilder() {
        Event.Builder builder = Event.newBuilder();
        this.commandUUID.ifPresent(commandUUID ->
            builder.setCommandUUID(
                ByteString.copyFrom(UUIDSerDe.serialize(commandUUID))
            )
        );

        return builder;
    }
}
