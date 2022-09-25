package edu.rit.se.sars.communication.message.swarm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationQueryMessageTest {

    protected static final LocationQueryMessage sampleLocationQueryMessage = new LocationQueryMessage();

    @Test
    public void testLocationQueryMessageSerDe() {
        assertEquals(
            sampleLocationQueryMessage,
            new LocationQueryMessage(sampleLocationQueryMessage.toProtobuf())
        );
    }
}
