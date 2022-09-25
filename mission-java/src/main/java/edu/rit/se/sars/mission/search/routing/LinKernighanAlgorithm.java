package edu.rit.se.sars.mission.search.routing;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.mission.consensus.PointTask;
import edu.rit.se.sars.mission.flight.path.FlightPath;
import edu.rit.se.sars.mission.flight.path.FlightPathPoint;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Lin-Kernighan routing algorithm wrapper
 */
public class LinKernighanAlgorithm implements RoutingAlgorithm {

    // Number of iterations to find 'global' min cost
    private static final int NUM_ITERATIONS = 100;

    @Override
    public FlightPath getPath(GeoLocation2D startLocation, GeoLocation2D endLocation, List<PointTask> points, double altitudeMeters) {
        points.add(0, new PointTask(
            startLocation,
            new Orientation(0, 0, 0),
            Optional.empty(),
            1.0
        ));

        double minDist = Double.MAX_VALUE;
        int[] minTour = null;

        // Find min cost over NUM_ITERATIONS
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            LinKernighan lk = new LinKernighan(points);
            lk.runAlgorithm();

            double tourDist = lk.getDistance();
            if (tourDist < minDist) {
                minDist = tourDist;
                minTour = lk.tour;
            }
        }

        int[] tour = Arrays.copyOf(minTour, minTour.length);
        // Rotate tour until tour starting point is desired starting point
        while (tour[0] != 0) {
            leftRotateByOne(tour);
        }

        BlockingQueue<FlightPathPoint> pathPoints = new LinkedBlockingQueue<>();
        for (int tourPointId : tour) {
            PointTask task = points.get(tourPointId);

            pathPoints.add(
                new FlightPathPoint(
                    new GeoLocation3D(
                        task.getLocation(),
                        altitudeMeters
                    ),
                    task.getGimbalOrientation(),
                    task.getHeadingDegrees(),
                    task.getZoomLevel()
                )
            );
        }

        // TODO: see if different start and end can be factored in by LKH
        // Add end location as last point in path
        pathPoints.add(
            new FlightPathPoint(
                new GeoLocation3D(endLocation, altitudeMeters),
                new Orientation(0, 0, 0),
                Optional.empty(),
                1.0
            )
        );

        return new FlightPath(pathPoints);
    }

    /**
     * Shift each element in the array left by one in-place, wrapping around at the end
     * @param arr Array to shift values of
     */
    private static void leftRotateByOne(int[] arr) {
        int temp = arr[0], i;
        for (i = 0; i < arr.length - 1; i++) {
            arr[i] = arr[i + 1];
        }
        arr[arr.length-1] = temp;
    }
}
