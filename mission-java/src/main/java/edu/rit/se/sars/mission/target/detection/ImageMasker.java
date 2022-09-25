package edu.rit.se.sars.mission.target.detection;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageMasker {
    //Used for getting OpenCV to run here, in this instance.
    static {
        nu.pattern.OpenCV.loadLocally();
    }
    //The hardcoded low HSV value for the threshold to include.
    public final Scalar low = new Scalar(0,10,0);
    //The hardcoded high HSV value for the threshold to include.
    public final Scalar high = new Scalar(255,255,255);
    //The minimum area of a detection to be considered valid.
    public final double minSize = 20;

    public final Scalar RED = new Scalar(0,0,255);
    public final Scalar GREEN = new Scalar(0,255,0);

    //A useless wrapper to load an image to an HSV matrix.
    public Mat imageToMat (String filename) {
        return Imgcodecs.imread(filename);
    }

    /**
     * Takes a matrix and returns the bounding boxes around regions in the color range.
     * @param frame: the image matrix to process.
     * @return: the list of all bounding boxes over the minimum size of colors in that range.
     */
    public List<Rect> processToRects(Mat frame) {
        Mat frameHSV = new Mat();
        //Moves the HSV image to the frameHSV matrix in BGR (RGB in reverse) color space.
        Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);
        Mat thresh = new Mat();
        //Writes a binary (in color range = 1, out of color range = 0) map to thresh.
        Core.inRange(frameHSV, low,
                high, thresh);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        //Uses a lightweight method to draw contour lines around the outlines of detected shapes.
        //Loads the list of all contour sets into contours.
        Imgproc.findContours(thresh,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        displayMatWithContours(thresh,contours,hierarchy);
        //Returns the bounding boxes of all contours which are over the minimum area.
        return contours.
                stream().
                filter(con -> areaLargeEnough(con,minSize)).
                map(Imgproc::boundingRect).
                collect(Collectors.toList());
    }

    /**
     * A partitioning algorithm that breaks one matrix into smaller rectangles.
     * This isn't really used and will struggle with detections on the edges.
     * Still cool, keeping it in.
     * @param input: the frame to operate on.
     * @param rows: the number of vertical slices to cut it into.
     * @param cols: the number of horizontal slices to cut it into.
     * @return: an array of matrices from the original.
     */
    public Mat [][] partition (Mat input,int rows, int cols) {
        int rowPixel = 0;
        int colPixel = 0;
        int rowGap = input.height() / rows;
        int colGap = input.width() / cols;
        int rowNew;
        int colNew;
        Mat [][] toReturn = new Mat[rows][cols];
        for (int row = 0; row < rows ; row ++ ){
            if (row == rows - 1) {
                rowNew = input.height();
            }else {
                rowNew = rowPixel + rowGap;
            }
            for (int col = 0 ; col < cols ; col ++ ){
                if (col == cols - 1) {
                    colNew = input.width();
                }else {
                    colNew = colPixel + colGap;
                }
                toReturn[row][col] = input.submat(rowPixel,rowNew,colPixel,colNew);
                colPixel = colNew;
            }

            rowPixel = rowNew;
            colPixel = 0;
        }
        return toReturn;
    }

    /**
     * Checks if a contour is large enough to count.
     * @param contour: the contour to check.
     * @param area: the minimum area to allow.
     * @return: if it is greater than or equal to the minimum area.
     */
    public boolean areaLargeEnough (Mat contour, double area) {
        return Imgproc.contourArea(contour) >= area;
    }

    /**
     * A nice little timing function for performance logging.
     * @param start: the start time in ns.
     * @param end: the end time in ns.
     * @return: the elapsed time in ms.
     */
    public double deltaMs(long start,long end){
        return (end - start) / 1000000.0;
    }

    public void displayMat (Mat mat) {
        HighGui.imshow("Matrix",mat);
        HighGui.waitKey();
    }

    public void displayMatWithContours (Mat mat, List<MatOfPoint> contours,Mat hierarchy) {
        Mat withColor = new Mat();
        Imgproc.cvtColor(mat,withColor,Imgproc.COLOR_GRAY2BGR);
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(withColor, contours, i, GREEN, 2, Imgproc.LINE_8, hierarchy, 0, new Point());
        }
        HighGui.imshow("Matrix",withColor);
        HighGui.waitKey();
    }

}