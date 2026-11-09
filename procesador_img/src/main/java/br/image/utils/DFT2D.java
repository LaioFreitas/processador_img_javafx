package br.image.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class DFT2D {

    public static class Spectrum {
        public final int width;
        public final int height;

        public final double[][] real;
        public final double[][] imaginary;

        public final WritableImage spectrumImage;

        public Spectrum(
                int width,
                int height,
                double[][] real,
                double[][] imaginary,
                WritableImage spectrumImage) {

            this.width = width;
            this.height = height;
            this.real = real;
            this.imaginary = imaginary;
            this.spectrumImage = spectrumImage;
        }
    }

    public static Spectrum transform(Image image) {

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();

        double[][] real = new double[height][width];
        double[][] imaginary = new double[height][width];

        // Converte a imagem para escala de cinza
        double[][] pixels = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color color = reader.getColor(x, y);

                pixels[y][x] = 0.299 * color.getRed() +
                        0.587 * color.getGreen() +
                        0.114 * color.getBlue();
            }
        }

        // DFT bidimensional
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {

                double sumReal = 0;
                double sumImaginary = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        double angle = 2.0 * Math.PI *
                                ((double) u * x / width +
                                        (double) v * y / height);

                        sumReal += pixels[y][x] * Math.cos(angle);

                        sumImaginary -= pixels[y][x] * Math.sin(angle);
                    }
                }

                real[v][u] = sumReal;
                imaginary[v][u] = sumImaginary;
            }
        }

        WritableImage spectrumImage = createSpectrumImage(real, imaginary, width, height);

        return new Spectrum(
                width,
                height,
                real,
                imaginary,
                spectrumImage);
    }

    private static WritableImage createSpectrumImage(
            double[][] real,
            double[][] imaginary,
            int width,
            int height) {

        WritableImage output = new WritableImage(width, height);

        double[][] magnitude = new double[height][width];

        double maxMagnitude = 0;

        // Calcula magnitude e centraliza o espectro
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double mag = Math.sqrt(
                        real[y][x] * real[y][x] +
                                imaginary[y][x] * imaginary[y][x]);

                // Escala logarítmica
                mag = Math.log1p(mag);

                int shiftedX = (x + width / 2) % width;
                int shiftedY = (y + height / 2) % height;

                magnitude[shiftedY][shiftedX] = mag;

                maxMagnitude = Math.max(maxMagnitude, mag);
            }
        }

        // Normalização para exibição
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double value = maxMagnitude > 0
                        ? magnitude[y][x] / maxMagnitude
                        : 0;

                output.getPixelWriter().setColor(
                        x, y,
                        Color.gray(value));
            }
        }

        return output;
    }
}
