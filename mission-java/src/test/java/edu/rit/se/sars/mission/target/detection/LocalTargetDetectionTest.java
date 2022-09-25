package edu.rit.se.sars.mission.target.detection;

import edu.rit.se.sars.communication.message.swarm.TargetDetectionMessage;
import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.target.detection.LocalTargetDetection;
import edu.rit.se.sars.test.RandomUtil;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LocalTargetDetectionTest {

    @Test
    public void testLocalTargetDetectionFromTargetDetectionMessages() throws IOException {
        int fileLength = 1024;
        int chunkSize = 128;

        File imageFile = File.createTempFile("detection", "png");

        byte[] imageBytes = RandomUtil.getRandomBytes(fileLength);
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            outputStream.write(imageBytes);
        }

        LocalTargetDetection initialLocalDetection = new LocalTargetDetection(
            UUID.randomUUID(),
            System.currentTimeMillis(),
            0.99,
            new GeoLocation2D(-34.56, 78.90),
            new Rectangle2D.Double(1,2,3,4),
            imageFile
        );

        Map<Integer, TargetDetectionMessage> chunkMessageMap = TargetDetectionMessage.fromLocalDetection(initialLocalDetection, chunkSize)
            .stream()
            .collect(Collectors.toMap(
                TargetDetectionMessage::getChunkIndex, m -> m
            ));

        File tmpDir = Files.createTempDirectory("mission").toFile();
        LocalTargetDetection reconstructedLocalDetection = LocalTargetDetection.fromTargetDetectionMessages(
            chunkMessageMap,
            tmpDir
        );

        assertEquals(reconstructedLocalDetection.getId(), initialLocalDetection.getId());
        assertEquals(reconstructedLocalDetection.getTimestampMillis(), initialLocalDetection.getTimestampMillis());
        assertEquals(reconstructedLocalDetection.getConfidence(), initialLocalDetection.getConfidence(), 0.0001);
        assertEquals(reconstructedLocalDetection.getLocation(), initialLocalDetection.getLocation());

        assertArrayEquals(
            Files.readAllBytes(reconstructedLocalDetection.getFile().toPath()),
            imageBytes
        );
    }
}
