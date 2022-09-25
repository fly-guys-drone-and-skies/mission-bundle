package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetOrientationCommandTest {
    @Test
    public void testGetOrientationCommandSerDe() {
        final GetOrientationCommand command = new GetOrientationCommand();

        assertEquals(command, new GetOrientationCommand(command.toProtobuf()));
    }
}
