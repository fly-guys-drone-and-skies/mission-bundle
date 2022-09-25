package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AcknowledgementMessageTest {

    protected static final AcknowledgementMessage sampleAcknowledgementMessage = new AcknowledgementMessage(
        UUID.randomUUID()
    );

    @Test
    public void testAcknowledgementMessageSerDe() {
        assertEquals(
            sampleAcknowledgementMessage,
            new AcknowledgementMessage(sampleAcknowledgementMessage.toProtobuf())
        );
    }
}
