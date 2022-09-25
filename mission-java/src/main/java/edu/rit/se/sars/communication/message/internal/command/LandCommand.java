package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

/**
 * Land drone at current position
 */
@EqualsAndHashCode(callSuper = true)
public class LandCommand extends DroneCommand {

    public LandCommand() {
        super();
    }

    protected LandCommand(Command command) {
        super(command);
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setLand(
                Command.LandCommand.getDefaultInstance()
            ).build();
    }
}
