package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

/**
 * Takeoff and hover at current location
 */
@EqualsAndHashCode(callSuper = true)
public class TakeoffCommand extends DroneCommand {

    public TakeoffCommand() {
        super();
    }

    protected TakeoffCommand(Command command) {
        super(command);
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setTakeoff(
                Command.TakeoffCommand.getDefaultInstance()
            ).build();
    }
}
