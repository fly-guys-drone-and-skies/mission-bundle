package edu.rit.se.sars.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeoLocation2DTest {
    @Test
    public void testGeoLocation2DSerDe() {
        final GeoLocation2D location = new GeoLocation2D(3.69, -2.1);

        assertEquals(new GeoLocation2D(location.toProtobuf()), location);
    }
}
