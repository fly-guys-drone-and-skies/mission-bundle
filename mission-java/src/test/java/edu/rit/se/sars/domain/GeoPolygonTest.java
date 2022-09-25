package edu.rit.se.sars.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeoPolygonTest {
    @Test
    public void testGeoPolygonSerDe() {
        final List<GeoLocation2D> locations = Arrays.asList(
            new GeoLocation2D(50.1, -30.0),
            new GeoLocation2D(-40.15, 90.1),
            new GeoLocation2D(15.6, 10.1)
        );
        final GeoPolygon polygon = new GeoPolygon(locations);

        assertEquals(new GeoPolygon(polygon.toProtobuf()), polygon);
    }
}
