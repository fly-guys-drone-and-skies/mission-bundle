package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingMessageTest {

    protected static final PingMessage samplePingMessage = new PingMessage();

    @Test
    public void testPingMessageSerDe() {
        assertEquals(samplePingMessage, new PingMessage(samplePingMessage.toProtobuf()));
    }
}
