package edu.rit.se.sars.mission.target.detection;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class ImageStreamProcessor {
    static {
        nu.pattern.OpenCV.loadLocally();
    }
    public static void main(String[] args) {
        String filename = "Pool_Test.m4v";
        String videoPath = System.getProperty("user.dir") + "/mission-java/src/main/resources/assets/videos/";
        String outputDir = "poolframes";
        int frameDigits = 2;
        try {
            String command = "ffmpeg -i " + videoPath + filename + " '" + videoPath + outputDir + "/%0" + String.valueOf(frameDigits) + "d.png'";
            String[] commands = new String[]{
                    "ffmpeg",
                    "-i",
                    videoPath + filename,
                    "'" + videoPath + outputDir + "/%0" + frameDigits + "d.png'"
            };
            Process pb = new ProcessBuilder("ffmpeg","-i",videoPath + filename,"'" + videoPath + outputDir + "/%0" + frameDigits + "d.png'").start();

            //Process toImages = Runtime.getRuntime().exec(commands);
            //printResults(toImages);
            Runtime.getRuntime().exec("ffmpeg -i /home/arctic/Downloads/RIT/Capstone/Repo/master/mission-java/src/main/resources/assets/videos/Pool_Test.m4v '/home/arctic/Downloads/RIT/Capstone/Repo/master/mission-java/src/main/resources/assets/videos/poolframes/%02d.png'");
            sleep(5000);
            printResults(Runtime.getRuntime().exec("ls"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
