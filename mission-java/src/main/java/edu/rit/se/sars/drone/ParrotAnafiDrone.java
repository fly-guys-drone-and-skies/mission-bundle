package edu.rit.se.sars.drone;

import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.opencv.videoio.Videoio.CAP_FFMPEG;

/**
 * Parrot ANAFI IPC implementation
 */
public class ParrotAnafiDrone extends Drone {

    public static final String droneName = "parrot-anafi";

    static {
        nu.pattern.OpenCV.loadLocally();
    }
    /**
     * @param droneAddress Address of Drone/SkyController
     * @param wrapperAddress SDK wrapper's server address
     */
    public ParrotAnafiDrone(InetAddress droneAddress, InetSocketAddress wrapperAddress) {
        super(droneAddress,wrapperAddress,ParrotAnafiDrone.class);
    }

    @Override
    public VideoCapture getVideoFeed() {
        VideoCapture capture = new VideoCapture();
        String streamLocation = String.format("rtsp://%s/live", this.getDroneAddress().getHostAddress());
        /*
          Since the CV model is too slow to process all frames from the video stream, a buffer size of 1
          is used to keep only the most recent frame.
         */
        capture.open(
                String.format(
                        "rtspsrc location=%s ! decodebin ! videoconvert ! appsink max-buffers=1 drop=true",
                        streamLocation
                ), CAP_FFMPEG
        );

        try {
            Runtime.getRuntime().exec("ffplay " + streamLocation + " -window_title parrot-anafi");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return capture;
    }

    @Override
    public CameraProperties getCameraProperties() {
        return new CameraProperties(
                69.0,
                57.0,
                new Dimension(1920,1080)
        );
    }

}