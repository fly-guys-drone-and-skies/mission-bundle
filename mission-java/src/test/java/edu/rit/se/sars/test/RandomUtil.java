package edu.rit.se.sars.test;

import java.util.Random;

public class RandomUtil {

    public static byte[] getRandomBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        new Random().nextBytes(bytes);

        return bytes;
    }

}
