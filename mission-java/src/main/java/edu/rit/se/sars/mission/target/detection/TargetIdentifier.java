package edu.rit.se.sars.mission.target.detection;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.drone.Drone;
import edu.rit.se.sars.drone.ParrotAnafiDrone;
import lombok.SneakyThrows;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

public abstract class TargetIdentifier extends Observable implements Runnable {
    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final Logger logger = LoggerFactory.getLogger(ParrotAnafiDrone.class);

    // Minimum confidence level for detection to be considered valid
    private static final double CONFIDENCE_THRESHOLD = 0.2;
    /**
     * Maximum number of detections in a frame for any of those detections to be considered valid.
     * When a frame is corrupted, the resulting noise throws off the CV model, generating many
     * false detections.
     */
    private static final int NOISE_DETECTION_THRESHOLD = 10;

    /**
     * Flag for locally persisting detections. If true, all detection images above the noise threshold will be saved.
     */
    private static final boolean saveDetections = true;
    /**
     * Output directory associated with the `saveDetections` flag.
     */
    private static final File outputDirectory = new File("cv_out");

    private final TargetType targetType;
    private final Drone drone;

    public TargetIdentifier(TargetType targetType, Drone drone) {
        this.targetType = targetType;
        this.drone = drone;
    }

    /**
     * Detect target in frame
     * @param frame Frame to look for target in
     * @return List of target detections in the frame
     */
    public abstract List<FrameDetection> process(Mat frame);

    @SneakyThrows
    @Override
    public void run() {
        VideoCapture capture = drone.getVideoFeed();
        DetectionPipeline pipeline = new DetectionPipeline(new Scalar(0,157,144), new Scalar(179,255,255), 5.5);
        int frameNum = 0;
        Mat currentFrame = new Mat();
        while (!Thread.currentThread().isInterrupted()) {
            capture.read(currentFrame);
            if (currentFrame.empty()) {
                continue;
            }

            List<DetectionPipeline.ScoredDetection> pipelineDetections = pipeline.processImage(currentFrame, drone);
            if (pipelineDetections.size() > 0) {
                File tempFile = File.createTempFile("detection", ".jpg");
                Imgcodecs.imwrite(tempFile.getPath(), currentFrame);
                DetectionPipeline.ScoredDetection detection = pipelineDetections.get(0);
                LocalTargetDetection localDetection = new LocalTargetDetection(
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        detection.score,
                        new GeoLocation2D(drone.getLocation()),
                        new Rectangle2D.Double(detection.boundingBox.x,detection.boundingBox.y,detection.boundingBox.width,detection.boundingBox.height),
                        tempFile
                );
                this.notifyObservers(localDetection);
            }

            long startMillis = System.currentTimeMillis();
            List<FrameDetection> detections = this.process(currentFrame);
            double elapsedSeconds = (System.currentTimeMillis() - startMillis) / 1000.0;

            double fps = 1 / elapsedSeconds;
            if (frameNum++ % 10 == 0) {
                logger.debug("FPS: {}", fps);
            }

            if (detections.size() > 0) {
                logger.debug("Detections: {}", detections.size());

                if (detections.stream().anyMatch(d -> d.getConfidence() >= CONFIDENCE_THRESHOLD)) {
                    try {
                        GeoLocation2D location = new GeoLocation2D(drone.getLocation());

                        File tempFile = File.createTempFile("detection", ".jpg");
                        Imgcodecs.imwrite(tempFile.getPath(), currentFrame);

                        // TODO: cleanup with java8 stream
                        FrameDetection maxConfidenceDetection = detections.get(0);
                        for (FrameDetection detection : detections) {
                            if (detection.getConfidence() > maxConfidenceDetection.getConfidence()) {
                                maxConfidenceDetection = detection;
                            }
                        }
                        LocalTargetDetection localDetection = new LocalTargetDetection(
                            UUID.randomUUID(),
                            System.currentTimeMillis(),
                            maxConfidenceDetection.getConfidence(),
                            location,
                            maxConfidenceDetection.getRectangle(),
                            tempFile
                        );

                        this.setChanged();
                        this.notifyObservers(localDetection);
                    } catch (Exception e) {
                        logger.error("Failed to store detection", e);
                    }
                }

                if (saveDetections) {
                    File outputFile = new File(
                        outputDirectory,
                        String.format("%d.png", System.currentTimeMillis())
                    );
                    Imgcodecs.imwrite(outputFile.getPath(), currentFrame);
                }
            }
        }
    }
}
