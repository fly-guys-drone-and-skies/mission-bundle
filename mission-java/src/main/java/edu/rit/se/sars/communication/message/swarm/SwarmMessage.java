package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class SwarmMessage implements ProtobufSerializable<edu.rit.se.sars.communication.proto.external.SwarmMessage> {

    protected edu.rit.se.sars.communication.proto.external.SwarmMessage.Builder getProtobufBuilder() {
        return edu.rit.se.sars.communication.proto.external.SwarmMessage.newBuilder();
    }

    /**
     * Convert protobuf type to associated message object type
     * @param message Protobuf message type
     * @return Swarm message object
     */
    public static SwarmMessage fromProtobuf(edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        switch (message.getMessageCase()) {
            case ACKNOWLEDGEMENT:
                return new AcknowledgementMessage(message);
            case PING:
                return new PingMessage(message);
            case MISSIONDEFINITION:
                return new MissionDefinitionMessage(message);
            case COVERAGESTATE:
                return new CoverageStateMessage(message);
            case LOCATIONQUERY:
                return new LocationQueryMessage(message);
            case LOCATIONSTATUS:
                return new LocationStatusMessage(message);
            case POINTCOMPLETED:
                return new PointCompletedMessage(message);
            case TARGETDETECTION:
                return new TargetDetectionMessage(message);
            case FOCUSDETECTIONCANDIDATE:
                return new FocusDetectionCandidateMessage(message);
            case FOCUSDETECTION:
                return new FocusDetectionMessage(message);
            case RETURNHOME:
                return new ReturnHomeMessage(message);
            case REMOVEDRONE:
                return new RemoveDroneMessage(message);
            default:
                throw new UnsupportedOperationException("Unexpected message type");
        }
    }
}
