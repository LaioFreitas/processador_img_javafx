package br.image.entities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Arrays;

import br.image.exceptions.ImageNullException;

public class Histogram {

    private final double[] red = new double[256];
    private final double[] green = new double[256];
    private final double[] blue = new double[256];

    public void calcular(Image imagem) {
        // Zera as contagens anteriores.
        Arrays.fill(red, 0);
        Arrays.fill(green, 0);
        Arrays.fill(blue, 0);

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

                red[r] += 1 / totalElements;
                green[g] += 1 / totalElements;
                blue[b] += 1 / totalElements;
            }
        }
    }

    public WritableImage histogramEqualization(Image imagem) {

        if (imagem == null || imagem.getPixelReader() == null) {
            throw new IllegalArgumentException("Não há imagem carregada");
        }

        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader reader = imagem.getPixelReader();

        int totalPixels = largura * altura;
        int[] pixels = new int[totalPixels];

        PixelFormat<java.nio.IntBuffer> formato = PixelFormat.getIntArgbInstance();

        // 1. Lê a imagem inteira.
        reader.getPixels(
                0, 0, largura, altura,
                PixelFormat.getIntArgbInstance(), pixels, 0, largura);

        // 2. Calcula o histograma em tons de cinza.
        long[] histograma = new long[256];

        for (int argb : pixels) {
            int r = (argb >>> 16) & 0xFF;
            int g = (argb >>> 8) & 0xFF;
            int b = argb & 0xFF;

            int cinza = (int) Math.round(
                    0.299 * r + 0.587 * g + 0.114 * b);

            histograma[cinza]++;
        }

        // 3. Calcula a CDF e encontra o primeiro valor não nulo.
        long[] cdf = new long[256];
        long acumulado = 0;
        long cdfMin = 0;
        boolean primeiro = false;

        for (int i = 0; i < 256; i++) {
            acumulado += histograma[i];
            cdf[i] = acumulado;

            if (!primeiro && histograma[i] > 0) {
                cdfMin = acumulado;
                primeiro = true;
            }
        }

        // 4. Cria a tabela de transformação.
        int[] transformacao = new int[256];
        long denominador = totalPixels - cdfMin;

        if (denominador <= 0) {
            return imagemParaTonsDeCinza(imagem);
        }

        for (int i = 0; i < 256; i++) {
            double valor = (double) (cdf[i] - cdfMin)
                    / denominador;

            transformacao[i] = (int) Math.round(
                    255.0 * Math.max(0.0, Math.min(1.0, valor)));
        }

        // 5. Aplica a transformação, preservando o alfa.
        for (int i = 0; i < pixels.length; i++) {
            int argb = pixels[i];

            int a = (argb >>> 24) & 0xFF;
            int r = (argb >>> 16) & 0xFF;
            int g = (argb >>> 8) & 0xFF;
            int b = argb & 0xFF;

            int cinza = (int) Math.round(
                    0.299 * r + 0.587 * g + 0.114 * b);

            int novoCinza = transformacao[cinza];

            pixels[i] = (a << 24)
                    | (novoCinza << 16)
                    | (novoCinza << 8)
                    | novoCinza;
        }

        // 6. Escreve a imagem processada.
        WritableImage resultado = new WritableImage(largura, altura);
        PixelWriter writer = resultado.getPixelWriter();

        writer.setPixels(
                0, 0, largura, altura,
                formato, pixels, 0, largura);

        return resultado;
    }

    private WritableImage imagemParaTonsDeCinza(Image imagem) {
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        WritableImage resultado = new WritableImage(largura, altura);
        PixelReader reader = imagem.getPixelReader();
        PixelWriter writer = resultado.getPixelWriter();

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int argb = reader.getArgb(x, y);

                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = argb & 0xFF;

                int cinza = (int) Math.round(
                        0.299 * r + 0.587 * g + 0.114 * b);

                writer.setArgb(
                        x, y,
                        (a << 24) | (cinza << 16)
                                | (cinza << 8) | cinza);
            }
        }

        return resultado;
    }

    public double[] getRed() {
        return red;
    }

    public double[] getGreen() {
        return green;
    }

    public double[] getBlue() {
        return blue;
    }
}
