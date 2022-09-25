package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointCompletedMessageTest {

    protected static final PointCompletedMessage samplePointCompletedMessage = new PointCompletedMessage(5);

    @Test
    public void testPointCompletedMessageSerDe() {
        assertEquals(
            samplePointCompletedMessage,
            new PointCompletedMessage(samplePointCompletedMessage.toProtobuf())
        );
    }
}
