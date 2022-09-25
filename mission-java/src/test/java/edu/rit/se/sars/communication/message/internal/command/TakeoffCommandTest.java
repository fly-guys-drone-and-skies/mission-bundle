package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TakeoffCommandTest {
    @Test
    public void testTakeoffCommandSerDe() {
        final TakeoffCommand command = new TakeoffCommand();

        assertEquals(command, new TakeoffCommand(command.toProtobuf()));
    }
}
