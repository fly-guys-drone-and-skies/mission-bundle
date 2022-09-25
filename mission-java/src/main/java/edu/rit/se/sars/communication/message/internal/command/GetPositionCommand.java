package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

/**
 * Get position (lat, lon, relative altitude) of drone
 */
@EqualsAndHashCode(callSuper = true)
public class GetPositionCommand extends DroneCommand {

    public GetPositionCommand() {
        super();
    }

    public GetPositionCommand(Command command) {
        super(command);
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setGetPosition(
                Command.GetPositionCommand.getDefaultInstance()
            ).build();
    }
}
