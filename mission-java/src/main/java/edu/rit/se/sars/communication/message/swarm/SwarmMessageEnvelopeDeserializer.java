package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.serde.MessageDeserializer;

import java.io.IOException;

/**
 * Deserializer for swarm message envelopes
 */
public class SwarmMessageEnvelopeDeserializer implements MessageDeserializer<SwarmMessageEnvelope> {

    @Override
    public SwarmMessageEnvelope deserialize(byte[] messageData) throws IOException {
        edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope envelope =
            edu.rit.se.sars.communication.proto.external.SwarmMessageEnvelope.parseFrom(messageData);

        return SwarmMessageEnvelope.fromProtobuf(envelope);
    }
}
