package edu.rit.se.sars.communication.message.internal.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandCompletedEventTest {
    @Test
    public void testCommandCompletedEventSerDe() {
        final CommandCompletedEvent event = new CommandCompletedEvent();

        assertEquals(new CommandCompletedEvent(event.toProtobuf()), event);
    }
}
