package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.communication.proto.ipc.command.Command;
import edu.rit.se.sars.domain.Orientation;
import lombok.EqualsAndHashCode;

/**
 * Change zoom level of drone camera
 */
@EqualsAndHashCode(callSuper = true)
public class ChangeZoomLevelCommand extends DroneCommand {

    private final double zoomLevel;

    /**
     * @param zoomLevel Zoom level to set camera to (1.0 = no zoom)
     */
    public ChangeZoomLevelCommand(double zoomLevel) {
        super();

        this.zoomLevel = zoomLevel;
    }

    protected ChangeZoomLevelCommand(Command command) {
        super(command);

        this.zoomLevel = command.getChangeZoomLevel().getZoomLevel();
    }

    @Override
    public Command toProtobuf() {
        return this.getProtobufBuilder()
            .setChangeZoomLevel(
                Command.ChangeZoomLevelCommand.newBuilder()
                    .setZoomLevel(this.zoomLevel)
            ).build();
    }
}
