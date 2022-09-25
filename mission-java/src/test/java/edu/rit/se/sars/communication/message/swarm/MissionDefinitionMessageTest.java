package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.communication.network.entity.IPEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntity;
import edu.rit.se.sars.communication.network.entity.NetworkEntityType;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoPolygon;
import edu.rit.se.sars.mission.Mission;
import edu.rit.se.sars.mission.target.detection.TargetType;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MissionDefinitionMessageTest {

    private static final Map<UUID, NetworkEntity> sampleNetworkEntities = new HashMap<UUID, NetworkEntity>() {{
        UUID operatorUUID = UUID.randomUUID();
        put(
            operatorUUID,
            new IPEntity(
                NetworkEntityType.OPERATOR,
                operatorUUID,
                new InetSocketAddress("127.0.0.1", 1234)
            )
        );

        UUID droneUUID = UUID.randomUUID();
        put(
            droneUUID,
            new IPEntity(
                NetworkEntityType.DRONE,
                droneUUID,
                new InetSocketAddress("127.0.0.1", 5678)
            )
        );
    }};

    private static final GeoPolygon sampleArea = new GeoPolygon(
        Arrays.asList(
            new GeoLocation2D(40.0, 40.0),
            new GeoLocation2D(40.05, 40.0),
            new GeoLocation2D(40.05, 40.05),
            new GeoLocation2D(40.0, 40.05)
        )
    );

    private static final Mission sampleMission = new Mission(
        sampleNetworkEntities,
        sampleArea,
        50,
        1.5,
        TargetType.PERSON
    );

    protected static final MissionDefinitionMessage sampleMissionDefinitionMessage = new MissionDefinitionMessage(sampleMission);

    @Test
    public void testMissionDefinitionMessageSerDe() {
        assertEquals(
            sampleMissionDefinitionMessage,
            new MissionDefinitionMessage(sampleMissionDefinitionMessage.toProtobuf())
        );
    }
}
