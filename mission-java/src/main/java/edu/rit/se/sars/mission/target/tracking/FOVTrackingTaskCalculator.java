package edu.rit.se.sars.mission.target.tracking;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.domain.GeoLocation3D;
import edu.rit.se.sars.domain.Orientation;
import edu.rit.se.sars.drone.CameraProperties;
import edu.rit.se.sars.mission.consensus.PointTask;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class FOVTrackingTaskCalculator implements TrackingTaskCalculator {

    @Override
    public PointTask getTrackingTask(
        GeoLocation3D droneLocation,
        Orientation droneOrientation,
        Orientation gimbalOrientation,
        CameraProperties cameraProperties,
        Rectangle2D targetBoundingBox
    ) {
        Dimension2D cameraDimensions = cameraProperties.getDimensions();

        double xDegreesPerPixel = cameraProperties.getHorizontalFOVDegrees() / cameraDimensions.getWidth();
        double yDegreesPerPixel = cameraProperties.getVerticalFOVDegrees() / cameraDimensions.getHeight();

        Point2D targetCenter = new Point2D.Double(
            targetBoundingBox.getCenterX() + 1,
            targetBoundingBox.getCenterY() + 1
        );
        double offsetXDegrees = (targetCenter.getX() - (cameraDimensions.getWidth() / 2)) * xDegreesPerPixel;
        double offsetYDegrees = (targetCenter.getY() - (cameraDimensions.getHeight() / 2)) * yDegreesPerPixel;

        Orientation focusGimbalOrientation = new Orientation(
            Math.round(gimbalOrientation.getPitchDegrees() + offsetYDegrees),
            0,
            0
        );
        double focusHeading = (droneOrientation.getYawDegrees() + offsetXDegrees) % 360;

        return new PointTask(
            new GeoLocation2D(droneLocation),
            focusGimbalOrientation,
            Optional.of(focusHeading),
            TrackingTaskCalculator.TRACKING_ZOOM_LEVEL
        );
    }
}
