package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.domain.GeoLocation3D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionStatusEventTest {
    @Test
    public void testPositionStatusEventSerDe() {
        final PositionStatusEvent event = new PositionStatusEvent(
            new GeoLocation3D(5.60, -49.1, 50)
        );

        assertEquals(new PositionStatusEvent(event.toProtobuf()), event);
    }
}
