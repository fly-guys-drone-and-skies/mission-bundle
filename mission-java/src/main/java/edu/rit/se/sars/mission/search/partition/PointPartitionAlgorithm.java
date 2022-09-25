package edu.rit.se.sars.mission.search.partition;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.search.partition.exception.NoSolutionException;

import java.util.List;

/**
 * Point load balancing algorithm for area allocation among the swarm
 */
public interface PointPartitionAlgorithm {
    List<List<GeoLocation2D>> partition(List<GeoLocation2D> points, List<GeoLocation2D> droneLocations) throws NoSolutionException;
}
