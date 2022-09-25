package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrientationStatusEventTest {
    @Test
    public void testOrientationStatusEventSerDe() {
        final OrientationStatusEvent event = new OrientationStatusEvent(
            new Orientation(1.23, 4.56, 6.78)
        );

        assertEquals(new OrientationStatusEvent(event.toProtobuf()), event);
    }
}
