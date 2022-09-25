package edu.rit.se.sars.mission.target.detection;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MaskingContoursTest {
    private final ImageMasker imageMasker = new ImageMasker();

    @Test
    public void testGetContoursFromImage () {
        Mat m1 = imageMasker.imageToMat(System.getProperty("user.dir") + "/src/test/java/edu/rit/se/sars/test/images/shapes.png");
        List<Rect> rects = imageMasker.processToRects(m1);
        assertEquals(rects.size(),6);
    }
}
