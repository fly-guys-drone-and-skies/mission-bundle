package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LandCommandTest {
    @Test
    public void testLandCommandSerDe() {
        final LandCommand command = new LandCommand();

        assertEquals(command, new LandCommand(command.toProtobuf()));
    }
}
