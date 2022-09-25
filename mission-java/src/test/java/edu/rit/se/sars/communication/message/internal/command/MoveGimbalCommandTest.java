package edu.rit.se.sars.communication.message.internal.command;

import edu.rit.se.sars.domain.Orientation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveGimbalCommandTest {
    @Test
    public void testMoveToCommandSerDe() {
        final MoveGimbalCommand command = new MoveGimbalCommand(
            new Orientation(40.1, 49.1, 14.8)
        );

        assertEquals(command, new MoveGimbalCommand(command.toProtobuf()));
    }
}
