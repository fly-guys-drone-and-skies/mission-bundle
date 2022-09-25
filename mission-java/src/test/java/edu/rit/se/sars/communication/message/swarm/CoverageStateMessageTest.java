package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.mission.consensus.CoverageState;
import edu.rit.se.sars.mission.consensus.PointAssignment;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.consensus.PointTask;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CoverageStateMessageTest {

    protected static final CoverageStateMessage sampleCoverageStateMessage = new CoverageStateMessage(
        new CoverageState(
            5,
            Arrays.asList(
                new PointAssignment(
                    UUID.randomUUID(),
                    1,
                    new PointTask(
                        new GeoLocation2D(34.5, -67.8),
                        new Orientation(1,2,3),
                        Optional.of(41.0),
                        1.0
                    )
                ),
                new PointAssignment(
                    UUID.randomUUID(),
                    2,
                    new PointTask(
                        new GeoLocation2D(4.5, -7.8),
                        new Orientation(1,2,3),
                        Optional.empty(),
                        1.0
                    )
                )
            ),
            new HashMap<UUID, Double>() {{
                put(UUID.randomUUID(), 22.5);
                put(UUID.randomUUID(), 66.21);
            }}
        )
    );

    @Test
    public void testCoverageStateMessageSerDe() {
        assertEquals(
            sampleCoverageStateMessage,
            new CoverageStateMessage(sampleCoverageStateMessage.toProtobuf())
        );
    }
}
