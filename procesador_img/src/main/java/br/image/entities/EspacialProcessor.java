package br.image.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.image.exceptions.ImageNullException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class EspacialProcessor extends ImageHandle {

    public EspacialProcessor() {
        super();
    }

    public Image mediaFilter(int m, int n) {
        if (image == null) {
            throw new ImageNullException();
        }

        int a = (m - 1) / 2;
        int b = (n - 1) / 2;
        int radius = m / 2;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage imageAux = new WritableImage(width + a * 2, height + b * 2);

        WritableImage imageProcessed = new WritableImage(width, height);

        populateImageAux(imageAux, radius);

        PixelReader readerImageAux = imageAux.getPixelReader();
        PixelWriter writerImageproc = imageProcessed.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float sumR = 0.0f;
                float sumG = 0.0f;
                float sumB = 0.0f;

                for (int dy = -radius; dy <= b; dy++) {
                    for (int dx = -radius; dx <= a; dx++) {
                        Color pixColor = readerImageAux.getColor(i + a + dx, j + b + dy);
                        sumR += pixColor.getRed();
                        sumG += pixColor.getGreen();
                        sumB += pixColor.getBlue();
                    }
                }

                sumR /= (m * n);
                sumG /= (m * n);
                sumB /= (m * n);

                sumR = Math.min(1.0f, Math.max(0.0f, sumR));
                sumG = Math.min(1.0f, Math.max(0.0f, sumG));
                sumB = Math.min(1.0f, Math.max(0.0f, sumB));

                Color pixel = new Color(sumR, sumG, sumB, 1.0);

                writerImageproc.setColor(i, j, pixel);
            }
        }

        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    public Image medianFilter(int kernelDim) {
        if (image == null) {
            throw new ImageNullException();
        }

        // int a = (kernelDim - 1) / 2;
        int radius = kernelDim / 2;

        WritableImage imageAuxImage = new WritableImage((int) image.getWidth() + radius * 2,
                (int) image.getHeight() + radius * 2);
        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());

        PixelReader readerImgAux = imageAuxImage.getPixelReader();

        PixelWriter writerImgProc = imageProcessed.getPixelWriter();

        populateImageAux(imageAuxImage, radius);

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                List<Double> listRedValue = new ArrayList<Double>();
                List<Double> listGreenValue = new ArrayList<Double>();
                List<Double> listBlueValue = new ArrayList<Double>();

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        listRedValue.add(readerImgAux.getColor(i + radius + dx, j + radius + dy).getRed());
                        listGreenValue.add(readerImgAux.getColor(i + radius + dx, j + radius + dy).getGreen());
                        listBlueValue.add(readerImgAux.getColor(i + radius + dx, j + radius + dy).getBlue());
                    }
                }

                listRedValue.sort(Comparator.naturalOrder());
                listGreenValue.sort(Comparator.naturalOrder());
                listBlueValue.sort(Comparator.naturalOrder());

                double red = listRedValue.get((kernelDim * kernelDim) / 2);
                double green = listGreenValue.get((kernelDim * kernelDim) / 2);
                double blue = listBlueValue.get((kernelDim * kernelDim) / 2);

                red = Math.min(1.0f, Math.max(0, red));
                green = Math.min(1.0f, Math.max(0, green));
                blue = Math.min(1.0f, Math.max(0, blue));

                Color pixel = new Color(red, green, blue, 1);

                writerImgProc.setColor(i, j, pixel);
            }
        }

        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    public Image laplacianFilter() {
        if (image == null) {
            System.out.println("ERROR: não há imagem carregada");
            return null;
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader readerImg = image.getPixelReader();

        float[][] kernel = {
                { 1, 1, 1 },
                { 1, -8, 1 },
                { 1, 1, 1 }
        };

        int radius = 1;
        int widthAux = width + 2 * radius;
        int heightAux = height + 2 * radius;

        // Matrizes auxiliares para os três canais.
        float[][] redAux = new float[heightAux][widthAux];
        float[][] greenAux = new float[heightAux][widthAux];
        float[][] blueAux = new float[heightAux][widthAux];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = readerImg.getColor(x, y);

                redAux[y + radius][x + radius] = (float) color.getRed();

                greenAux[y + radius][x + radius] = (float) color.getGreen();

                blueAux[y + radius][x + radius] = (float) color.getBlue();
            }
        }

        // Resultados do filtro por canal.
        float[][] redResult = new float[height][width];
        float[][] greenResult = new float[height][width];
        float[][] blueResult = new float[height][width];

        double minR = Double.POSITIVE_INFINITY;
        double maxR = Double.NEGATIVE_INFINITY;

        double minG = Double.POSITIVE_INFINITY;
        double maxG = Double.NEGATIVE_INFINITY;

        double minB = Double.POSITIVE_INFINITY;
        double maxB = Double.NEGATIVE_INFINITY;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double sumR = 0.0;
                double sumG = 0.0;
                double sumB = 0.0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int ay = y + radius + dy;
                        int ax = x + radius + dx;

                        float weight = kernel[dy + radius][dx + radius];

                        sumR += redAux[ay][ax] * weight;
                        sumG += greenAux[ay][ax] * weight;
                        sumB += blueAux[ay][ax] * weight;
                    }
                }

                redResult[y][x] = (float) sumR;
                greenResult[y][x] = (float) sumG;
                blueResult[y][x] = (float) sumB;

                minR = Math.min(minR, sumR);
                maxR = Math.max(maxR, sumR);

                minG = Math.min(minG, sumG);
                maxG = Math.max(maxG, sumG);

                minB = Math.min(minB, sumB);
                maxB = Math.max(maxB, sumB);
            }
        }

        WritableImage imageProcessed = new WritableImage(width, height);
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = normalize(
                        redResult[y][x], minR, maxR);

                double g = normalize(
                        greenResult[y][x], minG, maxG);

                double b = normalize(
                        blueResult[y][x], minB, maxB);

                Color result = new Color(r, g, b, 1.0);
                writerImgProc.setColor(x, y, result);
            }
        }

        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    public Image sobelFilter() {
        if (image == null) {
            throw new ImageNullException();
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();

        int radius = 1;

        int[][] kernelX = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
        };

        int[][] kernelY = {
                { -1, -2, -1 },
                { 0, 0, 0 },
                { 1, 2, 1 }
        };

        int WidthAux = width + 2 * radius;
        int heightAux = height + 2 * radius;

        double[][] redAux = new double[heightAux][WidthAux];
        double[][] greenAux = new double[heightAux][WidthAux];
        double[][] blueAux = new double[heightAux][WidthAux];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                redAux[y + radius][x + radius] = color.getRed();
                greenAux[y + radius][x + radius] = color.getGreen();
                blueAux[y + radius][x + radius] = color.getBlue();
            }
        }

        double[][] redResult = new double[height][width];
        double[][] greenResult = new double[height][width];
        double[][] blueResult = new double[height][width];

        double minR = Double.POSITIVE_INFINITY;
        double maxR = Double.NEGATIVE_INFINITY;

        double minG = Double.POSITIVE_INFINITY;
        double maxG = Double.NEGATIVE_INFINITY;

        double minB = Double.POSITIVE_INFINITY;
        double maxB = Double.NEGATIVE_INFINITY;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double gxR = 0, gyR = 0;
                double gxG = 0, gyG = 0;
                double gxB = 0, gyB = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int ay = y + radius + dy;
                        int ax = x + radius + dx;

                        int kx = kernelX[dy + radius][dx + radius];
                        int ky = kernelY[dy + radius][dx + radius];

                        double r = redAux[ay][ax];
                        double g = greenAux[ay][ax];
                        double b = blueAux[ay][ax];

                        gxR += r * kx;
                        gyR += r * ky;

                        gxG += g * kx;
                        gyG += g * ky;

                        gxB += b * kx;
                        gyB += b * ky;
                    }
                }

                double magnitudeR = Math.hypot(gxR, gyR);
                double magnitudeG = Math.hypot(gxG, gyG);
                double magnitudeB = Math.hypot(gxB, gyB);

                redResult[y][x] = magnitudeR;
                greenResult[y][x] = magnitudeG;
                blueResult[y][x] = magnitudeB;

                minR = Math.min(minR, magnitudeR);
                maxR = Math.max(maxR, magnitudeR);

                minG = Math.min(minG, magnitudeG);
                maxG = Math.max(maxG, magnitudeG);

                minB = Math.min(minB, magnitudeB);
                maxB = Math.max(maxB, magnitudeB);
            }
        }

        WritableImage imageProcessed = new WritableImage(width, height);
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = normalize(
                        redResult[y][x], minR, maxR);

                double g = normalize(
                        greenResult[y][x], minG, maxG);

                double b = normalize(
                        blueResult[y][x], minB, maxB);

                writerImgProc.setColor(x, y, new Color(r, g, b, 1.0));
            }
        }

        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    private void populateImageAux(WritableImage imageAux, int radius) {
        PixelWriter writer = imageAux.getPixelWriter();
        PixelReader reader = ImageHandle.image.getPixelReader();

        int width = (int) ImageHandle.image.getWidth();
        int height = (int) ImageHandle.image.getHeight();

        // Loop pelas COLUNAS (X)
        for (int x = 0; x < width; x++) {
            // Loop pelas LINHAS (Y)
            for (int y = 0; y < height; y++) {
                // CORREÇÃO: Passando (X, Y) na ordem correta
                writer.setColor(
                        x + radius,
                        y + radius,
                        reader.getColor(x, y));
            }
        }
    }

    private double normalize(double valor, double min, double max) {
        if (max == min) {
            return 0.0;
        }

        double resultado = (valor - min) / (max - min);

        return Math.max(0.0, Math.min(1.0, resultado));
    }

}
