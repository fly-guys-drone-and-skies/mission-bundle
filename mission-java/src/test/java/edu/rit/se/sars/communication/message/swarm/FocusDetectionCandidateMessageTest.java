package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.mission.consensus.PointTask;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FocusDetectionCandidateMessageTest {

    protected static final FocusDetectionCandidateMessage sampleFocusDetectionCandidateMessage = new FocusDetectionCandidateMessage(
        UUID.randomUUID(),
        new PointTask(
            new GeoLocation2D(1.1, 3.4),
            new Orientation(5,6,7),
            Optional.of(53.1),
            1.1
        )
    );

    @Test
    public void testFocusDetectionCandidateMessageSerDe() {
        assertEquals(
            sampleFocusDetectionCandidateMessage,
            new FocusDetectionCandidateMessage(sampleFocusDetectionCandidateMessage.toProtobuf())
        );
    }
}
