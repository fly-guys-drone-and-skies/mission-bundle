package edu.rit.se.sars.communication.message.swarm;

import edu.rit.se.sars.domain.GeoLocation2D;
import edu.rit.se.sars.mission.target.detection.LocalTargetDetection;
import edu.rit.se.sars.test.RandomUtil;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TargetDetectionMessageTest {

    protected static final TargetDetectionMessage sampleTargetDetectionMessage = new TargetDetectionMessage(
        UUID.randomUUID(),
        System.currentTimeMillis() - 100,
        new GeoLocation2D(-44.12345, 67.45678),
        0.95,
        0,
        1,
        RandomUtil.getRandomBytes((int) Math.pow(2, 17))
    );

    @Test
    public void testTargetDetectionMessageSerDe() {
        assertEquals(sampleTargetDetectionMessage, new TargetDetectionMessage(sampleTargetDetectionMessage.toProtobuf()));
    }

    @Test
    public void testTargetDetectionMessageFromLocalDetection() throws IOException {
        int chunkSize = 128;
        int fileLength = 1024;
        int expectedNumChunks = 8;

        File imageFile = File.createTempFile("detection", "png");

        byte[] imageBytes = RandomUtil.getRandomBytes(fileLength);
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            outputStream.write(imageBytes);
        }

        LocalTargetDetection localDetection = new LocalTargetDetection(
            UUID.randomUUID(),
            System.currentTimeMillis(),
            0.99,
            new GeoLocation2D(-34.56, 78.90),
            new Rectangle2D.Double(1,2,3,4),
            imageFile
        );


        List<TargetDetectionMessage> messages = TargetDetectionMessage.fromLocalDetection(localDetection, chunkSize);
        assertEquals(messages.size(), expectedNumChunks);

        for (int chunkIndex = 0; chunkIndex < expectedNumChunks; chunkIndex++) {
            TargetDetectionMessage message = messages.get(chunkIndex);

            assertEquals(message.getChunkIndex(), chunkIndex);
            assertEquals(message.getNumChunks(), expectedNumChunks);

            byte[] expectedChunk = Arrays.copyOfRange(imageBytes, chunkIndex * chunkSize, (chunkIndex + 1) * chunkSize);
            assertArrayEquals(message.getImageChunk(), expectedChunk);
        }
    }
}
