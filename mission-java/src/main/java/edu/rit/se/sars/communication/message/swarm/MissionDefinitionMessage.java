package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.mission.Mission;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mission parameters
 * Drones shall start mission upon receipt
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class MissionDefinitionMessage extends SwarmMessage {

    private final Mission mission;

    public MissionDefinitionMessage(final Mission mission) {
        super();

        this.mission = mission;
    }

    public MissionDefinitionMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(new Mission(message.getMissionDefinition()));
    }

    public Mission getMission() {
        return this.mission;
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setMissionDefinition(
                this.mission.toProtobuf()
            ).build();
    }
}
