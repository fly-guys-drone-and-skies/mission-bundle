package edu.rit.se.sars.mission.search.routing;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.mission.consensus.PointTask;
import edu.rit.se.sars.mission.flight.path.FlightPath;

import java.util.List;

/**
 * Drone path routing algorithm
 */
public interface RoutingAlgorithm {

    Orientation defaultGimbalOrientation = new Orientation(-60, 0, 0);

    /**
     * Construct path through points
     * @param startLocation Starting location
     * @param endLocation Ending location
     * @param points List of points to route through
     * @param altitudeMeters Altitude to construct path at
     * @return Flight path through all points with specified start and end locations
     */
    FlightPath getPath(GeoLocation2D startLocation, GeoLocation2D endLocation, List<PointTask> points, double altitudeMeters);
}
