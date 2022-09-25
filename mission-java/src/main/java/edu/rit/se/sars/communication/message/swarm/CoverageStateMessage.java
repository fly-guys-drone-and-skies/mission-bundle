package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.mission.consensus.CoverageState;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Current area distribution coverage state
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class CoverageStateMessage extends SwarmMessage {

    private final CoverageState coverageState;

    /**
     * @param coverageState Current swarm coverage state
     */
    public CoverageStateMessage(final CoverageState coverageState) {
        super();

        this.coverageState = coverageState;
    }

    public CoverageStateMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(new CoverageState(message.getCoverageState()));
    }

    public CoverageState getCoverageState() {
        return this.coverageState;
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setCoverageState(
                this.coverageState.toProtobuf()
            ).build();
    }
}
