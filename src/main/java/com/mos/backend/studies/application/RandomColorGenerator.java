package com.mos.backend.studies.application;

import java.util.Random;

public class RandomColorGenerator {

    private static final int COLOR_BOUND = 256;
    private static final String HEX_FORMAT = "#%02X%02X%02X";

    public static String generateRandomColor() {
        Random random = new Random();

        int r = random.nextInt(COLOR_BOUND);
        int g = random.nextInt(COLOR_BOUND);
        int b = random.nextInt(COLOR_BOUND);

        return String.format(HEX_FORMAT, r, g, b);
    }
}
