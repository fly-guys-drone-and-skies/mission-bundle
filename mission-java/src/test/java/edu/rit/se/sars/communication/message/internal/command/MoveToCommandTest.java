package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.domain.GeoLocation3D;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class MoveToCommandTest {
    @Test
    public void testMoveToCommandWithoutHeadingSerDe() {
        final MoveToCommand command = new MoveToCommand(
            new GeoLocation3D(40.1, 49.1, 14.8)
        );

        assertEquals(command, new MoveToCommand(command.toProtobuf()));
    }

    @Test
    public void testMoveToCommandWithHeadingSerDe() {
        final MoveToCommand command = new MoveToCommand(
            new GeoLocation3D(40.1, 49.1, 14.8),
            Optional.of(130.0)
        );

        assertEquals(command, new MoveToCommand(command.toProtobuf()));
    }
}
