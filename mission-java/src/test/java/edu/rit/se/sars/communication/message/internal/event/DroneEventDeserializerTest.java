package edu.rit.se.sars.communication.message.internal.event;

import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;
import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.test.RandomUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DroneEventDeserializerTest {

    private final ProtobufSerializer<DroneEvent> serializer = new ProtobufSerializer<>();
    private final DroneEventDeserializer deserializer = new DroneEventDeserializer();

    @Test
    public void testDeserializeBatteryStatusEvent() throws IOException {
        final BatteryStatusEvent event = new BatteryStatusEvent(50.1f);

        this.testDeserializeEvent(event);
    }

    @Test
    public void testDeserializePositionStatusEvent() throws IOException {
        final PositionStatusEvent event = new PositionStatusEvent(
            new GeoLocation3D(4.1, 50.1, 0.15)
        );

        this.testDeserializeEvent(event);
    }

    @Test
    public void testDeserializeOrientationStatusEvent() throws IOException {
        final OrientationStatusEvent event = new OrientationStatusEvent(
            new Orientation(4.1, 50.1, 0.15)
        );

        this.testDeserializeEvent(event);
    }

    @Test
    public void testDeserializeConnectionStateEvent() throws IOException {
        final ConnectionStateEvent event = new ConnectionStateEvent(true);

        this.testDeserializeEvent(event);
    }

    @Test
    public void testDeserializeCommandCompletedEvent() throws IOException {
        final CommandCompletedEvent event = new CommandCompletedEvent();

        this.testDeserializeEvent(event);
    }

    private void testDeserializeEvent(DroneEvent event) throws IOException {
        byte[] eventBytes = serializer.serialize(event);

        assertEquals(deserializer.deserialize(eventBytes), event);
    }
}
