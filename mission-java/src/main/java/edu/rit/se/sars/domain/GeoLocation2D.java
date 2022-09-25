package edu.rit.se.sars.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.rit.se.sars.communication.serde.protobuf.ProtobufSerializable;
import lombok.Data;

@Data
public class GeoLocation2D implements ProtobufSerializable<edu.rit.se.sars.communication.proto.common.domain.GeoLocation2D> {

    private static final double NAUTICAL_MILES_TO_METERS = 1852;

    protected final double latitude;
    protected final double longitude;

    /**
     * @param latitude Latitude (degrees east)
     * @param longitude Longitude (degrees north)
     */
    @JsonCreator
    public GeoLocation2D(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoLocation2D(edu.rit.se.sars.communication.proto.common.domain.GeoLocation2D location2D) {
        this(
            location2D.getLatitude(),
            location2D.getLongitude()
        );
    }

    public GeoLocation2D(GeoLocation3D location3D) {
        this(
            location3D.getLatitude(),
            location3D.getLongitude()
        );
    }

    /**
     * Calculate distance to specified location
     * Haversine formula implementation: https://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html
     * @param otherLocation Location to calculate distance to
     * @return Great circle distance from this point to specified location in meters
     */
    public double distMeters(GeoLocation2D otherLocation) {
        double y1 = Math.toRadians(this.getLongitude());
        double x1 = Math.toRadians(this.getLatitude());
        double y2 = Math.toRadians(otherLocation.getLongitude());
        double x2 = Math.toRadians(otherLocation.getLatitude());

        double a = Math.pow(Math.sin((x2-x1)/2), 2) +
                Math.cos(x1) * Math.cos(x2) * Math.pow(Math.sin((y2-y1)/2), 2);

        // great circle distance in radians
        double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        // convert back to degrees
        angle2 = Math.toDegrees(angle2);

        // each degree on a great circle of Earth is 60 nautical miles
        double distance2 = 60 * angle2;

        return distance2 * NAUTICAL_MILES_TO_METERS;
    }

    @Override
    public edu.rit.se.sars.communication.proto.common.domain.GeoLocation2D toProtobuf() {
        return edu.rit.se.sars.communication.proto.common.domain.GeoLocation2D.newBuilder()
            .setLatitude(this.latitude)
            .setLongitude(this.longitude)
            .build();
    }

    @Override
    public int hashCode() {
        return new Double(this.getLatitude()).hashCode() + new Double(this.getLongitude()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        final double tolerance = 0.000001;

        if (o instanceof GeoLocation2D) {
            GeoLocation2D otherLocation = (GeoLocation2D) o;
            double latitudeDiff = Math.abs(this.latitude - otherLocation.getLatitude());
            double longitudeDiff = Math.abs(this.longitude - otherLocation.getLongitude());

            return (latitudeDiff < tolerance) && (longitudeDiff < tolerance);
        } else {
            return false;
        }
    }
}
