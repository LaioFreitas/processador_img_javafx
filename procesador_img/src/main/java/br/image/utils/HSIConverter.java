package br.image.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class HSIConverter {

    public static class HSI {
        public double h;
        public double s;
        public double i;
        public double alpha;

        public HSI(double h, double s, double i) {
            this(h, s, i, 1.0);
        }

        public HSI(double h, double s, double i, double alpha) {
            this.h = h;
            this.s = s;
            this.i = i;
            this.alpha = alpha;
        }
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    // =========================================
    // RGB -> HSI
    // =========================================

    public static HSI rgbToHSI(Color color) {

        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        double intensity = (r + g + b) / 3.0;

        double min = Math.min(r, Math.min(g, b));

        double saturation = 0;

        if (intensity > 0) {
            saturation = 1.0 - (min / intensity);
        }

        double numerator = 0.5 * ((r - g) + (r - b));

        double denominator = Math.sqrt(
                (r - g) * (r - g) +
                        (r - b) * (g - b));

        double hue = 0;

        if (denominator > 1e-10) {

            double value = numerator / denominator;

            value = Math.max(-1, Math.min(1, value));

            double theta = Math.acos(value);

            if (b <= g) {
                hue = theta;
            } else {
                hue = 2 * Math.PI - theta;
            }
        }

        hue = Math.toDegrees(hue);

        return new HSI(
                hue,
                clamp(saturation),
                clamp(intensity),
                color.getOpacity());
    }

    // =========================================
    // HSI -> RGB
    // =========================================

    public static Color hsiToRGB(HSI hsi) {

        double h = ((hsi.h % 360) + 360) % 360;
        double s = clamp(hsi.s);
        double i = clamp(hsi.i);

        double r, g, b;

        if (h < 120) {

            double rad = Math.toRadians(h);

            b = i * (1 - s);

            r = i * (1 + s * Math.cos(rad) /
                    Math.cos(Math.toRadians(60) - rad));

            g = 3 * i - (r + b);

        } else if (h < 240) {

            double rad = Math.toRadians(h - 120);

            r = i * (1 - s);

            g = i * (1 + s * Math.cos(rad) /
                    Math.cos(Math.toRadians(60) - rad));

            b = 3 * i - (r + g);

        } else {

            double rad = Math.toRadians(h - 240);

            g = i * (1 - s);

            b = i * (1 + s * Math.cos(rad) /
                    Math.cos(Math.toRadians(60) - rad));

            r = 3 * i - (g + b);
        }

        return new Color(
                clamp(r),
                clamp(g),
                clamp(b),
                clamp(hsi.alpha));
    }

    // =========================================
    // IMAGE RGB -> IMAGE HSI
    // =========================================

    public static HSI[][] imageToHSI(Image image) {

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        HSI[][] pixels = new HSI[height][width];

        PixelReader reader = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixels[y][x] = rgbToHSI(
                        reader.getColor(x, y));
            }
        }

        return pixels;
    }

    // =========================================
    // IMAGE HSI -> IMAGE RGB
    // =========================================

    public static WritableImage imageToRGB(HSI[][] pixels) {

        int height = pixels.length;
        int width = pixels[0].length;

        WritableImage image = new WritableImage(width, height);

        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                writer.setColor(
                        x,
                        y,
                        hsiToRGB(pixels[y][x]));
            }
        }

        return image;
    }
}
