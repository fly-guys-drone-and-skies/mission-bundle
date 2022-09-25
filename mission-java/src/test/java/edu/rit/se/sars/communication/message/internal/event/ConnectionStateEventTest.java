package edu.rit.se.sars.communication.message.internal.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConnectionStateEventTest {
    @Test
    public void testConnectionStateEventSerDe() {
        final ConnectionStateEvent event = new ConnectionStateEvent(true);

        assertEquals(new ConnectionStateEvent(event.toProtobuf()), event);
    }
}
