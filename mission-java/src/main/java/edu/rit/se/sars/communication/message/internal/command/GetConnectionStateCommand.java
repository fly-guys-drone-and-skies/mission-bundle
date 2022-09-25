package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Get state of SDK connection to drone
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetConnectionStateCommand extends DroneCommand {

    public GetConnectionStateCommand() {
        super();
    }

    public GetConnectionStateCommand(Command command) {
        super(command);
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setGetConnectionState(
                Command.GetConnectionStateCommand.getDefaultInstance()
            ).build();
    }
}
