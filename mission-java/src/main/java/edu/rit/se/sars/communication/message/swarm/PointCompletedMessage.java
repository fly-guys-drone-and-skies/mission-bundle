package edu.rit.se.sars.communication.message.swarm;

import lombok.ToString;

/**
 * Message to be sent when a distributed point is completed by a drone
 */
@ToString
public class PointCompletedMessage extends SwarmMessage {

    private final int pointID;

    /**
     * @param pointID ID of completed point (from coverage state)
     */
    public PointCompletedMessage(int pointID) {
        super();

        this.pointID = pointID;
    }

    public PointCompletedMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(message.getPointCompleted().getPointID());
    }

    public int getPointID() {
        return this.pointID;
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setPointCompleted(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.PointCompletedMessage.newBuilder()
                    .setPointID(this.pointID)
                    .build()
            ).build();
    }
}
