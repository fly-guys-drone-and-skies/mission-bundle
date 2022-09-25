package edu.rit.se.sars.mission.target.detection;

import lombok.Data;

import java.awt.geom.Rectangle2D;

/**
 * Detection from a camera frame
 */
@Data
public class FrameDetection {
    private final Rectangle2D rectangle;
    private final double confidence;

    /**
     * @param rectangle Bounding box (pixels) of detection in frame
     * @param confidence Detection confidence percentage in range [0,1]
     */
    public FrameDetection(Rectangle2D rectangle, double confidence) {
        this.rectangle = rectangle;
        this.confidence = confidence;
    }
}
