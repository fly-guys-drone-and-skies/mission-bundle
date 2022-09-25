package edu.rit.se.sars.communication.message.swarm;

import com.google.protobuf.ByteString;
import edu.rit.se.sars.communication.serde.UUIDSerDe;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.target.detection.LocalTargetDetection;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Target detection information - sent for each detection
 */
@Getter
public class TargetDetectionMessage extends SwarmMessage {

    /**
     * Image data must be chunked for sending over the wire for protocols such as UDP
     * Must be < (65,527 - protobuf overhead) for UDP
     */
    private static final int DEFAULT_CHUNK_SIZE = (int) Math.pow(2, 15);

    private final UUID detectionUUID;
    private final long timestampMillis;
    private final GeoLocation2D location;
    private final double confidence;
    private final int chunkIndex;
    private final int numChunks;
    private final byte[] imageChunk;

    /**
     * @param detectionUUID UUID of detection
     * @param timestampMillis Time of detection
     * @param location Location of drone when target detected
     * @param confidence Detection confidence percentage in range [0, 1]
     * @param chunkIndex Index of sent image chunk
     * @param numChunks Total number of image chunks for this detection
     * @param imageChunk Image chunk data
     */
    public TargetDetectionMessage(
        UUID detectionUUID,
        long timestampMillis,
        GeoLocation2D location,
        double confidence,
        int chunkIndex,
        int numChunks,
        byte[] imageChunk
    ) {
        super();

        this.detectionUUID = detectionUUID;
        this.timestampMillis = timestampMillis;
        this.location = location;
        this.confidence = confidence;
        this.chunkIndex = chunkIndex;
        this.numChunks = numChunks;
        this.imageChunk = imageChunk;
    }

    public TargetDetectionMessage(final edu.rit.se.sars.communication.proto.external.SwarmMessage message) {
        this(
            UUIDSerDe.deserialize(message.getTargetDetection().getDetectionUUID()),
            message.getTargetDetection().getTimestamp(),
            new GeoLocation2D(
                message.getTargetDetection().getLocation()
            ),
            message.getTargetDetection().getConfidence(),
            message.getTargetDetection().getChunkIndex(),
            message.getTargetDetection().getNumChunks(),
            message.getTargetDetection().getImageChunk().toByteArray()
        );
    }

    @Override
    public edu.rit.se.sars.communication.proto.external.SwarmMessage toProtobuf() {
        return this.getProtobufBuilder()
            .setTargetDetection(
                edu.rit.se.sars.communication.proto.external.SwarmMessage.TargetDetectionMessage.newBuilder()
                    .setDetectionUUID(
                        ByteString.copyFrom(UUIDSerDe.serialize(this.detectionUUID))
                    )
                    .setTimestamp(this.timestampMillis)
                    .setLocation(this.location.toProtobuf())
                    .setConfidence(this.confidence)
                    .setChunkIndex(this.chunkIndex)
                    .setNumChunks(this.numChunks)
                    .setImageChunk(ByteString.copyFrom(this.imageChunk))
                    .build()
            ).build();
    }

    /**
     * Convert raw locally stored detection to target detection messages (one for each image chunk)
     * @param detection Detection to convert
     * @return List of target detection messages representing the locally stored detection
     */
    public static List<TargetDetectionMessage> fromLocalDetection(LocalTargetDetection detection) throws IOException {
        return fromLocalDetection(detection, DEFAULT_CHUNK_SIZE);
    }

    /**
     * Convert raw locally stored detection to target detection messages (one for each image chunk)
     * @param detection Detection to convert
     * @param chunkSize Chunk size to use for image data
     * @return List of target detection messages representing the locally stored detection
     */
    public static List<TargetDetectionMessage> fromLocalDetection(LocalTargetDetection detection, int chunkSize) throws IOException {
        File imageFile = detection.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        int numChunks = (int) Math.ceil(imageFile.length() / (double) chunkSize);

        List<TargetDetectionMessage> messages = new LinkedList<>();

        int chunkIndex = 0;
        byte[] chunk = new byte[chunkSize];
        int currentChunkLength;
        while ((currentChunkLength = inputStream.read(chunk)) != -1) {
            messages.add(new TargetDetectionMessage(
                detection.getId(),
                detection.getTimestampMillis(),
                detection.getLocation(),
                detection.getConfidence(),
                chunkIndex++,
                numChunks,
                Arrays.copyOf(chunk, currentChunkLength)
            ));
        }

        return messages;
    }
}
