package edu.rit.se.sars.mission.target.detection;

import edu.rit.se.sars.drone.Drone;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * HAAR Full Body target identifier
 * https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades/haarcascade_fullbody.xml
 */
public class HAARTargetIdentifier extends TargetIdentifier {
    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final String modelPath =
        HAARTargetIdentifier.class.getResource("/cv/haar/haarcascade_fullbody.xml").getPath();

    private final CascadeClassifier classifier;

    public HAARTargetIdentifier(Drone drone) {
        super(TargetType.PERSON, drone);

        this.classifier = new CascadeClassifier(modelPath);
    }

    @Override
    public List<FrameDetection> process(Mat frame) {
        MatOfRect locations = new MatOfRect();
        MatOfInt numDetections = new MatOfInt();

        classifier.detectMultiScale2(frame, locations, numDetections);

        List<FrameDetection> frameDetections = new ArrayList<>();
        if (!locations.size().empty()) {
            List<Rect> locationsList = locations.toList();
            List<Integer> numDetectionsList = numDetections.toList();

            for (int i = 0; i < locationsList.size(); i++) {
                Rect rect = locationsList.get(i);
                int detections = numDetectionsList.get(i);

                frameDetections.add(
                    new FrameDetection(
                        new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height),
                        detections
                    )
                );
            }
        }

        return frameDetections;
    }
}
