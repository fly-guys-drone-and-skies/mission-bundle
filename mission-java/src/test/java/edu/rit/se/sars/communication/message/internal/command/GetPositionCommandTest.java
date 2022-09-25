package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetPositionCommandTest {

    @Test
    public void testGetPositionCommandSerDe() {
        final GetPositionCommand command = new GetPositionCommand();

        assertEquals(command, new GetPositionCommand(command.toProtobuf()));
    }
}
