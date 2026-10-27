package br.image.entities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import java.util.Arrays;

public class Histogram {

    private final double[] vermelho = new double[256];
    private final double[] verde = new double[256];
    private final double[] azul = new double[256];

    public void calcular(Image imagem) {
        // Zera as contagens anteriores.
        Arrays.fill(vermelho, 0);
        Arrays.fill(verde, 0);
        Arrays.fill(azul, 0);

        if (imagem == null) {
            return;
        }

        PixelReader reader = imagem.getPixelReader();

        int width = (int) imagem.getWidth();
        int height = (int) imagem.getHeight();
        int[] pixels = new int[width];
        double totalElements = width * height;

        for (int y = 0; y < height; y++) {
            reader.getPixels(0, y, width, 1, PixelFormat.getIntArgbInstance(), pixels, 0, width);
            for (int x = 0; x < width; x++) {

                // int a = (pixels[x] >> 24) & 0xff;
                int r = (pixels[x] >> 16) & 0xff;
                int g = (pixels[x] >> 8) & 0xff;
                int b = pixels[x] & 0xff;

                vermelho[r] += 1 / totalElements;
                verde[g] += 1 / totalElements;
                azul[b] += 1 / totalElements;
            }
        }
    }

    public double[] getVermelho() {
        return vermelho;
    }

    public double[] getVerde() {
        return verde;
    }

    public double[] getAzul() {
        return azul;
    }
}
