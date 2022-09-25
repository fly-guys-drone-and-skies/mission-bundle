package edu.rit.se.sars.mission.target.tracking;

import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.drone.CameraProperties;
import edu.rit.se.sars.mission.consensus.PointTask;
import edu.rit.se.sars.mission.flight.path.FlightPathPoint;

import java.awt.geom.Rectangle2D;

public interface TrackingTaskCalculator {

    double TRACKING_ZOOM_LEVEL = 1.4;

    PointTask getTrackingTask(
        GeoLocation3D droneLocation,
        Orientation droneOrientation,
        Orientation gimbalOrientation,
        CameraProperties cameraProperties,
        Rectangle2D targetBoundingBox
    );
}
