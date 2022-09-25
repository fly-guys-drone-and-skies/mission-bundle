package edu.rit.se.sars.mission.target.detection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.rit.se.sars.communication.message.swarm.TargetDetectionMessage;
import edu.rit.se.sars.domain.GeoLocation2D;
import lombok.Data;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Locally-stored (filesystem) target detection
 */
@Data
public class LocalTargetDetection implements Comparable<LocalTargetDetection> {

    private final UUID id;
    private final long timestampMillis;
    private final double confidence;
    private final GeoLocation2D location;
    @JsonIgnore
    private final Rectangle2D boundingBox;
    @JsonIgnore
    private final File file;

    /**
     * @param id Detection ID
     * @param timestampMillis Timestamp of detection
     * @param confidence Confidence of detection (percentage in range [0,1])
     * @param location Approximate location of detection
     * @param boundingBox Target bounding box
     * @param file Detection image file (full frame)
     */
    public LocalTargetDetection(
        UUID id,
        long timestampMillis,
        double confidence,
        GeoLocation2D location,
        Rectangle2D boundingBox, // TODO: need to add to frame detection proto?
        File file
    ) {
        this.id = id;
        this.timestampMillis = timestampMillis;
        this.confidence = confidence;
        this.location = location;
        this.boundingBox = boundingBox;
        this.file = file;
    }

    /**
     * Reconstruct target detection from target detection messages (chunks of same detection)
     * @param messages Map of chunk indexes to their associated detection message - should all have same detection ID
     * @param outputDirectory Directory to write constructed image file to
     * @return Reconstructed target detection
     */
    public static LocalTargetDetection fromTargetDetectionMessages(Map<Integer, TargetDetectionMessage> messages, File outputDirectory) throws IOException {
        TargetDetectionMessage firstMessage = messages.get(0);

        File outputFile = File.createTempFile("detection", null, outputDirectory);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            for (int chunkIndex = 0; chunkIndex < firstMessage.getNumChunks(); chunkIndex++) {
                outputStream.write(messages.get(chunkIndex).getImageChunk());
            }
        }

        return new LocalTargetDetection(
            firstMessage.getDetectionUUID(),
            firstMessage.getTimestampMillis(),
            firstMessage.getConfidence(),
            firstMessage.getLocation(),
            null,
            outputFile
        );
    }

    @Override
    public int compareTo(LocalTargetDetection o) {
        return (int) (this.timestampMillis - o.timestampMillis);
    }
}
