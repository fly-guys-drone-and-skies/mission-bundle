package edu.rit.se.sars.communication.message.internal.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BatteryStatusEventTest {
    @Test
    public void testBatteryStatusEventSerDe() {
        final BatteryStatusEvent event = new BatteryStatusEvent(55.5f);

        assertEquals(new BatteryStatusEvent(event.toProtobuf()), event);
    }
}
