package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

/**
 * Change camera gimbal orientation
 */
@EqualsAndHashCode(callSuper = true)
public class MoveGimbalCommand extends DroneCommand {

    private final Orientation toOrientation;

    /**
     * @param toOrientation Orientation to move gimbal to (relative)
     */
    public MoveGimbalCommand(Orientation toOrientation) {
        super();

        this.toOrientation = toOrientation;
    }

    protected MoveGimbalCommand(Command command) {
        super(command);

        this.toOrientation = new Orientation(
            command.getMoveGimbal().getToOrientation()
        );
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setMoveGimbal(
                Command.MoveGimbalCommand.newBuilder()
                    .setToOrientation(
                        this.toOrientation.toProtobuf()
                    ).build()
            ).build();
    }
}
