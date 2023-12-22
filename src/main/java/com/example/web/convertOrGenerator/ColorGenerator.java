package com.example.web.convertOrGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorGenerator {
    private static List<String> colors = new ArrayList<>();
    public static List<String> generateRandomColors(int count) {
        if (!colors.isEmpty()){
            return colors;
        }
        colors = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String color = generateRandomColor(random);
            colors.add(color);
        }

        return colors;
    }

    private static String generateRandomColor(Random random) {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return String.format("#%02x%02x%02x", red, green, blue);
    }

    public static List<String> getRandomColors(int numberOfRandomColor) {
        List<String> randomColors = generateRandomColors(numberOfRandomColor);
        return randomColors;
    }
}

