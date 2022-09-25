package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChangeZoomLevelCommandTest {
    @Test
    public void testChangeZoomLevelCommandSerDe() {
        final ChangeZoomLevelCommand command = new ChangeZoomLevelCommand(1.3);

        assertEquals(command, new ChangeZoomLevelCommand(command.toProtobuf()));
    }
}
