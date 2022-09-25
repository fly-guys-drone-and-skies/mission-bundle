package edu.rit.se.sars.mission.flight.path;

import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import lombok.Data;

import java.util.Optional;

/**
 * Single point and drone configuration in flight path
 */
@Data
public class FlightPathPoint {
    private final GeoLocation3D location;
    private final Orientation gimbalOrientation;
    private final Optional<Double> headingDegrees;
    private final double zoomLevel;

    /**
     * @param location Point location
     * @param gimbalOrientation Gimbal orientation to use while flying to point
     * @param headingDegrees Heading to use while flying to point, defaults to angle of location
     * @param zoomLevel Camera zoom level
     */
    public FlightPathPoint(
        GeoLocation3D location,
        Orientation gimbalOrientation,
        Optional<Double> headingDegrees,
        double zoomLevel
    ) {
        this.location = location;
        this.gimbalOrientation = gimbalOrientation;
        this.headingDegrees = headingDegrees;
        this.zoomLevel = zoomLevel;
    }
}
