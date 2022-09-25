package edu.rit.se.sars.mission.flight.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Flight path containing unvisited points
 */
public class FlightPath {

    private BlockingQueue<FlightPathPoint> points;

    /**
     * @param points In-order queue of points
     */
    public FlightPath(final BlockingQueue<FlightPathPoint> points) {
        this.points = points;
    }

    /**
     * @param points In-order list of points
     */
    public FlightPath(final List<FlightPathPoint> points) {
        this(new LinkedBlockingQueue<>(points));
    }

    public FlightPath() {
        this(new LinkedBlockingQueue<>());
    }

    /**
     * Retrieve and remove next point to visit in flight path
     * @return Next point in flight path if present, none if no points remain
     */
    public Optional<FlightPathPoint> getNextPoint() {
        return Optional.ofNullable(this.points.poll());
    }

    /**
     * @return All remaining flight path points
     */
    public Queue<FlightPathPoint> getPoints() {
        return this.points;
    }

    public List<FlightPathPoint> getPointList () {
        return new ArrayList<>(this.points);
    }

    /**
     * Add point to end of flight path
     * @param point Point to add
     */
    public void addPoint(FlightPathPoint point) {
        this.points.add(point);
    }

    /**
     * Override current queue of points
     * @param newPoints New points representing complete flight path
     */
    public void setPoints(final BlockingQueue<FlightPathPoint> newPoints) {
        this.points = newPoints;
    }

    /**
     * @return Number of remaining points
     */
    public int size() {
        return this.points.size();
    }
}
