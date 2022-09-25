package edu.rit.se.sars.communication.network.entity;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

public class NetworkEntityTest {

    public static final NetworkEntity sampleIPEntityDrone = new IPEntity(
        NetworkEntityType.DRONE,
        new InetSocketAddress("127.0.0.1", 10000)
    );

    public static final NetworkEntity sampleIPEntityOperator = new IPEntity(
        NetworkEntityType.OPERATOR,
        new InetSocketAddress("127.0.0.1", 10001)
    );

    @Test
    public void testNetworkEntitySerDe() {
        assertEquals(NetworkEntity.fromProtobuf(sampleIPEntityDrone.toProtobuf()), sampleIPEntityDrone);
        assertEquals(NetworkEntity.fromProtobuf(sampleIPEntityOperator.toProtobuf()), sampleIPEntityOperator);
    }
}
