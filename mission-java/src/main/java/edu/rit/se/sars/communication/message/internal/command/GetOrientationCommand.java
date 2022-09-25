package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

/**
 * Get absolute orientation/attitude of drone
 */
@EqualsAndHashCode(callSuper = true)
public class GetOrientationCommand extends DroneCommand {

    public GetOrientationCommand() {
        super();
    }

    public GetOrientationCommand(Command command) {
        super(command);
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setGetOrientation(
                Command.GetOrientationCommand.getDefaultInstance()
            ).build();
    }
}
