package edu.rit.se.sars.communication.message.internal.command;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import edu.rit.se.sars.communication.proto.ipc.command.Command;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * IPC drone command
 */
@Data
public abstract class DroneCommand implements ProtobufSerializable<Command> {

    private final UUID uuid;

    /**
     * @param uuid UUID of command instance (used for relaying command completion)
     */
    protected DroneCommand(UUID uuid) {
        this.uuid = uuid;
    }

    protected DroneCommand(Command command) {
        this(
            UUIDSerDe.deserialize(
                command.getUuid().toByteArray()
            )
        );
    }

    public DroneCommand() {
        this(UUID.randomUUID());
    }

    protected Command.Builder getProtobufBuilder() {
        return Command.newBuilder()
            .setUuid(
                ByteString.copyFrom(
                    UUIDSerDe.serialize(this.uuid)
                )
            );
    }
}
