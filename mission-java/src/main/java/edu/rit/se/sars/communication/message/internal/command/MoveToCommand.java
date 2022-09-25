package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.communication.proto.ipc.command.Command;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * Move drone to specified position
 */
@EqualsAndHashCode(callSuper = true)
public class MoveToCommand extends DroneCommand {

    private final GeoLocation3D toLocation;
    private final Optional<Double> headingDegrees;

    /**
     * @param toLocation Location to move to
     * @param headingDegrees Optional heading to maintain during movement
     */
    public MoveToCommand(final GeoLocation3D toLocation, final Optional<Double> headingDegrees) {
        super();

        this.toLocation = toLocation;
        this.headingDegrees = headingDegrees;
    }

    /**
     * Move to specified location with location as heading
     * @param toLocation Location to move to
     */
    public MoveToCommand(final GeoLocation3D toLocation) {
        this(toLocation, Optional.empty());
    }

    protected MoveToCommand(Command command) {
        super(command);

        this.toLocation = new GeoLocation3D(
            command.getMoveTo().getToLocation()
        );
        if (command.getMoveTo().hasHeadingDegrees()) {
            this.headingDegrees = Optional.of(command.getMoveTo().getHeadingDegrees());
        } else {
            this.headingDegrees = Optional.empty();
        }
    }

    @Override
    public Command toProtobuf() {
        Command.MoveToCommand.Builder moveToBuilder = Command.MoveToCommand.newBuilder()
            .setToLocation(
                this.toLocation.toProtobuf()
            );
        this.headingDegrees.ifPresent(moveToBuilder::setHeadingDegrees);

        return this.getProtobufBuilder()
            .setMoveTo(moveToBuilder)
            .build();
    }
}
