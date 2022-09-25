package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Remove drone from mission (e.g. after battery level triggers return to home)
 * Shall trigger re-distribution of removed drone's remaining points if applicable
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class RemoveDroneMessage extends SwarmMessage {

    private final NetworkEntity drone;

    /**
     * @param drone Drone to remove from mission
     */
    public RemoveDroneMessage(NetworkEntity drone) {
        super();

        this.drone = drone;
    }

    public RemoveDroneMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            NetworkEntity.fromProtobuf(
                message.getRemoveDrone().getDrone()
            )
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setRemoveDrone(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.RemoveDroneMessage.newBuilder()
                    .setDrone(this.drone.toProtobuf())
            ).build();
    }
}
