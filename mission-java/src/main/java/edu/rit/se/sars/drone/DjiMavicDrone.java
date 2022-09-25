package edu.rit.se.sars.drone;

import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.opencv.videoio.Videoio.CAP_FFMPEG;

public class DjiMavicDrone extends Drone{

    public static final String droneName = "dji-mavic";

    static {
        nu.pattern.OpenCV.loadLocally();
    }

    public DjiMavicDrone(InetAddress droneAddress, InetSocketAddress wrapperAddress) {
        super(droneAddress, wrapperAddress, DjiMavicDrone.class);
    }

    //These are just taken from the parrot for now.

    @Override
    public CameraProperties getCameraProperties() {
        return new CameraProperties(
                69.0,
                57.0,
                new Dimension(1920,1080)
        );
    }

    @Override
    public VideoCapture getVideoFeed() {
        VideoCapture capture = new VideoCapture();

        /*
          Since the CV model is too slow to process all frames from the video stream, a buffer size of 1
          is used to keep only the most recent frame.
         */
        capture.open(
                String.format(
                        "rtspsrc location=rtsp://%s/live ! decodebin ! videoconvert ! appsink max-buffers=1 drop=true",
                        this.getDroneAddress().getHostAddress()
                ), CAP_FFMPEG
        );

        return capture;
    }
}