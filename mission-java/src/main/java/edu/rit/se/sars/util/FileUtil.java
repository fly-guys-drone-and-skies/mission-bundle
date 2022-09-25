package edu.rit.se.sars.util;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class FileUtil {

    /**
     * Get absolute filesystem path of classpath resource
     * @param resourcePath Resource path in classpath
     * @return Absolute filesystem path of resource
     */
    public static String getResourceAbsolutePath(String resourcePath) {
        try {
            return Paths.get(
                FileUtil.class.getResource(resourcePath).toURI()
            ).toFile().getAbsolutePath();
        } catch (URISyntaxException e) {
            // Should never happen
            return null;
        }
    }
}
