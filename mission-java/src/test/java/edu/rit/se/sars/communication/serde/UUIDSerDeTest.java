package edu.rit.se.sars.communication.serde;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UUIDSerDeTest {

    @Test
    public void testUUIDSerDe() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = UUIDSerDe.serialize(uuid);

        assertEquals(uuid, UUIDSerDe.deserialize(uuidBytes));
    }
}
