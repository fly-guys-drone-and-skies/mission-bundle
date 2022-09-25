package edu.rit.se.sars.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeoLocation3DTest {
    @Test
    public void testGeoLocation3DSerDe() {
        final GeoLocation3D location = new GeoLocation3D(3.69, -2.1, 50.8);

        assertEquals(new GeoLocation3D(location.toProtobuf()), location);
    }
}
