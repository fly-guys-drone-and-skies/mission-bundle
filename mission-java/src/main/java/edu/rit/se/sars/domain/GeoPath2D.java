package edu.rit.se.sars.domain;

/**
 * Utility methods for path calculations
 */
public class GeoPath2D {

    /**
     * Calculate turn angle from point b -> c with reference point a
     * @param a First point in path
     * @param b Second point in path
     * @param c Third point in path
     * @return Relative turn angle to point c in radians, positive for either direction
     */
    public static double getTurnAngleRadians(GeoLocation2D a, GeoLocation2D b, GeoLocation2D c) {
        double r = Math.pow(a.longitude - b.longitude, 2) + Math.pow(a.latitude - b.latitude, 2);
        double s = Math.pow(b.longitude - c.longitude, 2) + Math.pow(b.latitude - c.latitude, 2);
        double t = Math.pow(c.longitude - a.longitude, 2) + Math.pow(c.latitude - a.latitude, 2);

        return Math.PI - Math.acos(
            (r + s - t) / Math.sqrt(4 * r * s)
        );
    }

    /**
     * Calculate turn angle from point b -> c with reference point a
     * @param a First point in path
     * @param b Second point in path
     * @param c Third point in path
     * @return Relative turn angle to point c in degrees, positive for either direction
     */
    public static double getTurnAngleDegrees(GeoLocation2D a, GeoLocation2D b, GeoLocation2D c) {
        return getTurnAngleRadians(a, b, c) * (180 / Math.PI);
    }
}
