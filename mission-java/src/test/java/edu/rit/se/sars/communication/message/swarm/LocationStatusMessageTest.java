package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.domain.GeoLocation3D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationStatusMessageTest {
    protected static final LocationStatusMessage sampleLocationStatusMessage = new LocationStatusMessage(
        new GeoLocation3D(12.3, -45.6, 78.9)
    );

    @Test
    public void testLocationStatusMessageSerDe() {
        assertEquals(
            sampleLocationStatusMessage,
            new LocationStatusMessage(sampleLocationStatusMessage.toProtobuf())
        );
    }
}
