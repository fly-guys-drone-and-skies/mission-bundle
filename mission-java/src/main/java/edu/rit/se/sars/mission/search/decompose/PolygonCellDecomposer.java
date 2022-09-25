package edu.rit.se.sars.mission.search.decompose;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoPolygon;
import edu.rit.se.sars.domain.GeoRectangle;
import edu.rit.se.sars.drone.CameraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Polygon approximation with squares
 * Any given square shall be visible from its center point at the provided altitude
 */
public class PolygonCellDecomposer implements PolygonDecomposer {

    @Override
    public List<GeoLocation2D> decompose(GeoPolygon area, double altitudeMeters, CameraProperties cameraProperties) {
        List<GeoLocation2D> points = new ArrayList<>();

        // Calculate cell sizes with vertical FOV, giving some frame overlap horizontally
        double cellSizeMeters = calculateCellSize(cameraProperties.getVerticalFOVDegrees(), altitudeMeters);

        // Get polygon bounding rectangle to construct a grid of lat/lon points
        GeoRectangle boundingRectangle = area.getBoundingRectangle();
        GeoLocation2D upperLeft = boundingRectangle.getUpperLeft();
        GeoLocation2D lowerLeft = boundingRectangle.getLowerLeft();
        GeoLocation2D upperRight = boundingRectangle.getUpperRight();

        // Traverse grid with points spaced at calculated cell size, keeping only points that are WITHIN the polygon
        int latitudeSteps = (int) (upperLeft.distMeters(lowerLeft) / cellSizeMeters);
        double latitudeSpacing = (upperLeft.getLatitude() - lowerLeft.getLatitude()) / latitudeSteps;
        for (double latitude = lowerLeft.getLatitude() + latitudeSpacing / 2; latitude <= upperLeft.getLatitude(); latitude += latitudeSpacing) {
            GeoLocation2D left = new GeoLocation2D(latitude, upperLeft.getLongitude());
            GeoLocation2D right = new GeoLocation2D(latitude, upperRight.getLongitude());

            int longitudeSteps = (int) (left.distMeters(right) / cellSizeMeters);
            double longitudeSpacing = (right.getLongitude() - left.getLongitude()) / longitudeSteps;

            for (double longitude = left.getLongitude() + longitudeSpacing / 2; longitude <= right.getLongitude(); longitude += longitudeSpacing) {
                GeoLocation2D point = new GeoLocation2D(latitude, longitude);
                if (area.contains(point)) {
                    // Since the bounding box is being used as a grid, some calculated points will be outside its bounds.
                    // These points should be ignored.
                    points.add(point);
                }
            }
        }

        return points;
    }

    /**
     * Calculate square cell size at given height with FOV angle
     * @param angleDegrees FOV angle to calculate with (used for vertical and horizontal)
     * @param h Height to calculate from
     * @return Side length of square
     */
    private static double calculateCellSize(double angleDegrees, double h) {
        double A = 2 * Math.pow(h / Math.tan(angleDegrees * (Math.PI / 180.0)), 2);

        return Math.sqrt(A);
    }
}
