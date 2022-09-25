package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.network.entity.NetworkEntityTest;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SwarmMessageEnvelopeDeserializerTest {

    private final ProtobufSerializer<SwarmMessageEnvelope> serializer = new ProtobufSerializer<>();
    private final SwarmMessageEnvelopeDeserializer deserializer = new SwarmMessageEnvelopeDeserializer();

    @Test
    public void testDeserializeAcknowledgementMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(AcknowledgementMessageTest.sampleAcknowledgementMessage);
    }

    @Test
    public void testDeserializePingMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(PingMessageTest.samplePingMessage);
    }

    @Test
    public void testDeserializeMissionDefinitionMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(MissionDefinitionMessageTest.sampleMissionDefinitionMessage);
    }

    @Test
    public void testDeserializeCoverageStateMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(CoverageStateMessageTest.sampleCoverageStateMessage);
    }

    @Test
    public void testDeserializeLocationQueryMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(LocationQueryMessageTest.sampleLocationQueryMessage);
    }

    @Test
    public void testDeserializeLocationStatusMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(LocationStatusMessageTest.sampleLocationStatusMessage);
    }

    @Test
    public void testDeserializePointCompletedMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(PointCompletedMessageTest.samplePointCompletedMessage);
    }

    @Test
    public void testDeserializeTargetDetectionMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(TargetDetectionMessageTest.sampleTargetDetectionMessage);
    }

    @Test
    public void testDeserializeReturnHomeMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(ReturnHomeMessageTest.sampleReturnHomeMessage);
    }

    @Test
    public void testDeserializeRemoveDroneMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(RemoveDroneMessageTest.sampleRemoveDroneMessage);
    }

    @Test
    public void testDeserializeFocusDetectionMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(FocusDetectionMessageTest.sampleFocusDetectionMessage);
    }

    @Test
    public void testDeserializeFocusDetectionCandidateMessage() throws IOException {
        this.testDeserializeSwarmMessageEnvelope(FocusDetectionCandidateMessageTest.sampleFocusDetectionCandidateMessage);
    }

    private void testDeserializeSwarmMessageEnvelope(SwarmMessage message) throws IOException {
        SwarmMessageEnvelope envelope = new SwarmMessageEnvelope(
            NetworkEntityTest.sampleIPEntityDrone,
            NetworkEntityTest.sampleIPEntityOperator,
            message
        );

        byte[] eventBytes = serializer.serialize(envelope);

        assertEquals(deserializer.deserialize(eventBytes), envelope);
    }

    @Test
    public void testDeserializeSwarmMessageEnvelopeEmptyToEntity() throws IOException {
        SwarmMessageEnvelope envelope = new SwarmMessageEnvelope(
            NetworkEntityTest.sampleIPEntityDrone,
            new PingMessage()
        );

        byte[] eventBytes = serializer.serialize(envelope);

        assertEquals(deserializer.deserialize(eventBytes), envelope);
    }
}
