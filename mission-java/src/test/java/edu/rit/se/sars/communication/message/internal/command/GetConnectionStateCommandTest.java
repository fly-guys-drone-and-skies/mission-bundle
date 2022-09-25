package edu.rit.se.sars.communication.message.internal.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetConnectionStateCommandTest {

    @Test
    public void testGetConnectionStateCommandSerDe() {
        final GetConnectionStateCommand command = new GetConnectionStateCommand();

        assertEquals(command, new GetConnectionStateCommand(command.toProtobuf()));
    }
}
