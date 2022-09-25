package edu.rit.se.sars.mission.search.decompose;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoPolygon;
import edu.rit.se.sars.drone.CameraProperties;

import java.util.List;

/**
 * Polygon point decomposition for approximation
 */
public interface PolygonDecomposer {
    /**
     * Decompose polygon into points for searching
     * @param area Area polygon to decompose
     * @param altitudeMeters Altitude for FOV calculations
     * @param cameraProperties Camera properties to use for FOV calculations
     * @return List of points representing decomposed polygon
     */
    List<GeoLocation2D> decompose(GeoPolygon area, double altitudeMeters, CameraProperties cameraProperties);
}
