package edu.rit.se.sars.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrientationTest {
    @Test
    public void testOrientationSerDe() {
        final Orientation orientation = new Orientation(50.77, 25.22, -59.145);

        assertEquals(new Orientation(orientation.toProtobuf()), orientation);
    }
}
