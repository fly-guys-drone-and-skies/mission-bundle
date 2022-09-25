package edu.rit.se.sars.mission.target.detection;

import edu.rit.se.sars.drone.Drone;
import edu.rit.se.sars.util.FileUtil;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import static org.opencv.core.CvType.CV_32F;

/**
 * Okutama-Action model target identifier
 * http://okutama-action.org/
 */
public class OkuTargetIdentifier extends TargetIdentifier {

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final String caffeProto = "/cv/okutama-action/deploy.prototxt";
    private static final String caffeModel = "/cv/okutama-action/VGG_okutama_SSD_512x512_iter_20000.caffemodel";

    /**
     * Minimum confidence required for detection to be considered
     * This does NOT override the overall confidence needed, but helps filters out the expected many low confidence
     *   (~0.01) detections that the model will output.
     */
    private static final double CONFIDENCE_THRESHOLD = 0.2;

    /**
     * Mean color value for transformation, defined by model training parameters (train.prototxt)
     */
    private static final Scalar meanValue = new Scalar(104.0, 117.0, 123.0, 0);

    private final Net net;

    public OkuTargetIdentifier(Drone drone) {
        super(TargetType.PERSON, drone);

        this.net = Dnn.readNetFromCaffe(
            FileUtil.getResourceAbsolutePath(caffeProto),
            FileUtil.getResourceAbsolutePath(caffeModel)
        );
    }

    /**
     * Implementation based on:
     *   https://github.com/mesutpiskin/opencv-object-detection/blob/bfc6d6e582f0319f12a8e9645cc45220886a4a70/src/DeepNeuralNetwork/DnnProcessor.java#L107-L125
     */
    @Override
    public List<FrameDetection> process(Mat frame) {
        Mat blob = Dnn.blobFromImage(frame, 1.0, new Size(512, 512), meanValue, false, false, CV_32F);
        int cols = frame.cols();
        int rows = frame.rows();

        Mat detections;
        synchronized (net) {
            net.setInput(blob);

            detections = net.forward();
            detections = detections.reshape(1, (int) detections.total() / 7);
        }

        List<FrameDetection> frameDetections = new LinkedList<>();

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence >= CONFIDENCE_THRESHOLD) {
                int left   = (int)(detections.get(i, 3)[0] * cols);
                int top    = (int)(detections.get(i, 4)[0] * rows);
                int right  = (int)(detections.get(i, 5)[0] * cols);
                int bottom = (int)(detections.get(i, 6)[0] * rows);

                frameDetections.add(
                    new FrameDetection(
                        new Rectangle2D.Double(left, top, right - left, bottom - top),
                        confidence
                    )
                );
            }
        }

        return frameDetections;
    }
}
