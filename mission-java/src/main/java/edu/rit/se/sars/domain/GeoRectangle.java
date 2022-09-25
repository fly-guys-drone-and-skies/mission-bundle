package edu.rit.se.sars.domain;

import lombok.Data;

/**
 * Rectangle defined by lat,lon points
 */
@Data
public class GeoRectangle {

    private final GeoLocation2D lowerLeft;
    private final GeoLocation2D upperRight;

    /**
     * @param lowerLeft Lower left (south-west) corner point
     * @param upperRight Upper right (north-east) corner point
     */
    public GeoRectangle(GeoLocation2D lowerLeft, GeoLocation2D upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }

    public GeoLocation2D getLowerLeft() {
        return this.lowerLeft;
    }

    public GeoLocation2D getLowerRight() {
        return new GeoLocation2D(this.lowerLeft.getLatitude(), this.upperRight.getLongitude());
    }

    public GeoLocation2D getUpperLeft() {
        return new GeoLocation2D(this.upperRight.getLatitude(), this.lowerLeft.getLongitude());
    }

    public GeoLocation2D getUpperRight() {
        return this.upperRight;
    }

    /**
     * @return Point at center of rectangle
     */
    public GeoLocation2D getCenter() {
        double centerLatitude = lowerLeft.getLatitude() + ((upperRight.getLatitude() - lowerLeft.getLatitude()) / 2);
        double centerLongitude = lowerLeft.getLongitude() + ((upperRight.getLongitude() - lowerLeft.getLongitude()) / 2);

        return new GeoLocation2D(centerLatitude, centerLongitude);
    }
}
