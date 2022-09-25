package edu.rit.se.sars.drone;

import lombok.Data;

import java.awt.geom.Dimension2D;

@Data
public class CameraProperties {
    private final double horizontalFOVDegrees;
    private final double verticalFOVDegrees;
    private final Dimension2D dimensions;
}
