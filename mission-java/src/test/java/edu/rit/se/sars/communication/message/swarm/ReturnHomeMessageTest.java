package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReturnHomeMessageTest {

    protected static final ReturnHomeMessage sampleReturnHomeMessage = new ReturnHomeMessage();

    @Test
    public void testReturnHomeMessageSerDe() {
        assertEquals(sampleReturnHomeMessage, new ReturnHomeMessage(sampleReturnHomeMessage.toProtobuf()));
    }
}
