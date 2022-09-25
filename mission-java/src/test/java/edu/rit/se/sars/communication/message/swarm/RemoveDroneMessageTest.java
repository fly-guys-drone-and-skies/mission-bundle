package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.network.entity.NetworkEntityTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveDroneMessageTest {

    protected static final RemoveDroneMessage sampleRemoveDroneMessage = new RemoveDroneMessage(
        NetworkEntityTest.sampleIPEntityDrone
    );

    @Test
    public void testRemoveDroneMessageSerDe() {
        assertEquals(
            sampleRemoveDroneMessage,
            new RemoveDroneMessage(sampleRemoveDroneMessage.toProtobuf())
        );
    }
}
