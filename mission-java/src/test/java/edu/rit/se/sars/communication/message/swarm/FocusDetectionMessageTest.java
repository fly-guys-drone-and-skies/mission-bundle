package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FocusDetectionMessageTest {

    protected static final FocusDetectionMessage sampleFocusDetectionMessage = new FocusDetectionMessage(UUID.randomUUID());

    @Test
    public void testFocusDetectionMessageSerDe() {
        assertEquals(sampleFocusDetectionMessage, new FocusDetectionMessage(sampleFocusDetectionMessage.toProtobuf()));
    }
}
