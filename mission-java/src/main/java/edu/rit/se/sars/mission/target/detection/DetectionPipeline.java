package edu.rit.se.sars.mission.target.detection;

import edu.rit.se.sars.communication.network.protocol.ProtocolStrategy;
import edu.rit.se.sars.drone.Drone;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.videoio.Videoio.CAP_FFMPEG;
import static org.opencv.videoio.Videoio.CAP_GSTREAMER;

public class DetectionPipeline {
    static {
        nu.pattern.OpenCV.loadLocally();
    }
    public final Scalar lower;
    public final Scalar upper;
    public final double minDetection;
    private static final Logger logger = LoggerFactory.getLogger(DetectionPipeline.class);

    public DetectionPipeline (Scalar lower, Scalar upper, double detectionThreshold) {
        this.lower = lower;
        this.upper = upper;
        this.minDetection = detectionThreshold;
    }

    public static class ScoredDetection implements Comparable<ScoredDetection>{
        public final MatOfPoint contour;
        public final double score;
        public final Rect boundingBox;

        public ScoredDetection (MatOfPoint contour, double score, Rect boundingBox) {
            this.contour = contour;
            this.score = score;
            this.boundingBox = boundingBox;
        }


        @Override
        public int compareTo(ScoredDetection scoredDetection) {
            return Double.compare(score, scoredDetection.score);
        }
    }

    private Mat reduceThreshold (Mat image) {
        Mat virginFrame = image.clone();
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
        Mat thresh = new Mat();
        Core.inRange(image, lower, upper, thresh);
        Mat result = new Mat();
        Core.bitwise_and(virginFrame,virginFrame,result,thresh);
        return thresh;
    }

    private List<MatOfPoint> reduceShapes (Mat image, Drone drone) {
        List<MatOfPoint> contourList = new ArrayList<>();
        Mat unusedHierarchy = new Mat();
        Imgproc.findContours(image,contourList,unusedHierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_TC89_KCOS);
//        List<MatOfPoint> mats = contourList.stream().filter(c -> {
//            double presumedArea = calculatePresumedArea(drone);
//            double contourArea = Imgproc.contourArea(c);
//            return 0.3 * presumedArea < contourArea && contourArea < 2 * presumedArea;
//        }).collect(Collectors.toList());
        List<MatOfPoint> mats = contourList.stream().filter(c -> {
            double cA = Imgproc.contourArea(c);
            return Imgproc.contourArea(c) > 80;
        }).sorted(Comparator.comparingDouble(Imgproc::contourArea)).collect(Collectors.toList());;

        Mat contourImg = new Mat(image.size(), image.type());
        for (int i = 0; i < mats.size(); i++) {
            Imgproc.drawContours(contourImg, mats, i, new Scalar(255, 0, 0), 3);
        }
        //int key = HighGui.waitKey(0);
        MatOfPoint sub = mats.size() > 0 ? mats.get(mats.size() - 1) : null;
//        if (key == 83 && sub != null) {
//            polygonToFile(sub);
//        }
        mats.forEach(m -> {
            logger.debug(m.toString());
        });
        return mats;
    }

    public String polygonToFile (MatOfPoint mat) {
        List<Point> points = mat.toList();
        List<String> pointStrings = points.stream().map(Point::toString).collect(Collectors.toList());
        String fileBody = String.join("\n",pointStrings);
        try {
            writeToFile("data.csv",fileBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToFile(String filename, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(text);
        writer.close();
    }

    public Point pointFromString (String str) {
        String cleaned = str.replaceAll(" ","").replaceAll("\\{","").replaceAll("}","");
        List<String> parts = Arrays.asList(cleaned.split(","));
        double x = Double.parseDouble(parts.get(0));
        double y = Double.parseDouble(parts.get(1));
        return new Point(x,y);

    }

    public MatOfPoint contourFromFile (String data) {
        List<String> pointStrings = Arrays.asList(data.split("\n").clone());
        return new MatOfPoint(pointStrings.stream().map(this::pointFromString).toArray(Point[]::new));
    }

    private List<MatOfPoint> getSavedContours () {
        Path filePath = Path.of("data.csv");
        try {
            String content = Files.readString(filePath);
            return List.of(contourFromFile(content));


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Stub.
        //Loads contours from file and returns their list.
        return new ArrayList<>();
    }

    private double calculatePresumedArea (Drone drone) {
        //Stub.
        //Given height, angle, approximate sub size, returns presumed pixel area.
        return 0;
    }

    private List<ScoredDetection> scoreDetections (List<MatOfPoint> contours) {
        List<MatOfPoint> matchAgainst = getSavedContours();
        return contours.stream().map(c->{
            double acc = 0;
            for (MatOfPoint againstContour : matchAgainst) {
                acc += Imgproc.matchShapes(c, againstContour, Imgproc.CONTOURS_MATCH_I2, 0.0);
            }
            return new ScoredDetection(c, acc/matchAgainst.size(), Imgproc.boundingRect(c));
        })
                .filter(s -> s.score < minDetection)
                .sorted(ScoredDetection::compareTo)
                .collect(Collectors.toList());
    }

    public List<ScoredDetection> processImage (Mat image, Drone drone) {
        Mat coloredThreshold = reduceThreshold(image);
        List<MatOfPoint> contours = reduceShapes(coloredThreshold, drone);
        return scoreDetections(contours).stream().filter(d -> d.score > minDetection).collect(Collectors.toList());
    }

//    public static void main(String[] args) {
//        DetectionPipeline dp = new DetectionPipeline(new Scalar(0,101,126), new Scalar(42,255,255),10.0);
//        VideoCapture capture = new VideoCapture(0);
////        capture.open(
////                "udp://127.0.0.1:23000", CAP_FFMPEG
////        );
//        System.out.println(capture.isOpened());
//        Mat im = new Mat();
//        while (capture.read(im)) {
//            System.out.println(dp.processImage(im,null).get(0).score);
//        }
//    }




}
