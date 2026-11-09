package br.image.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FFT2D {

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

    // Próxima potência de 2
    private static int nextPowerOfTwo(int n) {
        int result = 1;

        while (result < n) {
            result <<= 1;
        }

        return result;
    }

    // FFT unidimensional - Cooley-Tukey
    private static void fft(
            double[] real,
            double[] imag,
            boolean inverse) {
        int n = real.length;

        // Bit reversal
        int j = 0;

        for (int i = 1; i < n; i++) {
            int bit = n >> 1;

            while ((j & bit) != 0) {
                j ^= bit;
                bit >>= 1;
            }

            j ^= bit;

            if (i < j) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;

                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Butterfly
        for (int len = 2; len <= n; len <<= 1) {

            double angle = 2.0 * Math.PI / len;

            if (!inverse) {
                angle = -angle;
            }

            double wReal = Math.cos(angle);
            double wImag = Math.sin(angle);

            for (int i = 0; i < n; i += len) {

                double uReal = 1.0;
                double uImag = 0.0;

                for (int k = 0; k < len / 2; k++) {

                    int even = i + k;
                    int odd = even + len / 2;

                    double vReal = real[odd] * uReal -
                            imag[odd] * uImag;

                    double vImag = real[odd] * uImag +
                            imag[odd] * uReal;

                    real[odd] = real[even] - vReal;
                    imag[odd] = imag[even] - vImag;

                    real[even] += vReal;
                    imag[even] += vImag;

                    double nextReal = uReal * wReal - uImag * wImag;

                    double nextImag = uReal * wImag + uImag * wReal;

                    uReal = nextReal;
                    uImag = nextImag;
                }
            }
        }

        // Normalização da transformada inversa
        if (inverse) {
            for (int i = 0; i < n; i++) {
                real[i] /= n;
                imag[i] /= n;
            }
        }
    }

    // FFT bidimensional
    public static Spectrum transform(Image image) {

        if (image == null) {
            throw new IllegalArgumentException(
                    "Nenhuma imagem foi carregada.");
        }

        int originalWidth = (int) image.getWidth();
        int originalHeight = (int) image.getHeight();

        int width = nextPowerOfTwo(originalWidth);
        int height = nextPowerOfTwo(originalHeight);

        double[][] real = new double[height][width];
        double[][] imaginary = new double[height][width];

        PixelReader reader = image.getPixelReader();

        // 1. Conversão para escala de cinza
        // 2. Preenchimento com zeros (zero padding)
        for (int y = 0; y < originalHeight; y++) {
            for (int x = 0; x < originalWidth; x++) {

                int argb = reader.getArgb(x, y);

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                double gray = 0.299 * r +
                        0.587 * g +
                        0.114 * b;

                real[y][x] = gray;
                imaginary[y][x] = 0.0;
            }
        }

        // 3. FFT nas linhas
        for (int y = 0; y < height; y++) {

            fft(real[y], imaginary[y], false);

        }

        // 4. FFT nas colunas
        double[] columnReal = new double[height];
        double[] columnImag = new double[height];

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height; y++) {
                columnReal[y] = real[y][x];
                columnImag[y] = imaginary[y][x];
            }

            fft(columnReal, columnImag, false);

            for (int y = 0; y < height; y++) {
                real[y][x] = columnReal[y];
                imaginary[y][x] = columnImag[y];
            }
        }

        // 5. Criar imagem do espectro
        WritableImage spectrum = createSpectrumImage(real, imaginary, width, height);

        return new Spectrum(
                width,
                height,
                real,
                imaginary,
                spectrum);
    }

    // Visualização do espectro de magnitude
    private static WritableImage createSpectrumImage(
            double[][] real,
            double[][] imaginary,
            int width,
            int height) {

        WritableImage result = new WritableImage(width, height);

        PixelWriter writer = result.getPixelWriter();

        double maxMagnitude = 0.0;

        // Encontrar magnitude máxima
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double magnitude = Math.log1p(
                        Math.hypot(real[y][x], imaginary[y][x]));

                maxMagnitude = Math.max(
                        maxMagnitude,
                        magnitude);
            }
        }

        if (maxMagnitude == 0.0) {
            return result;
        }

        // Centralização das frequências
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int sourceX = (x + width / 2) % width;
                int sourceY = (y + height / 2) % height;

                double magnitude = Math.log1p(
                        Math.hypot(
                                real[sourceY][sourceX],
                                imaginary[sourceY][sourceX]));

                int intensity = (int) Math.round(
                        (magnitude / maxMagnitude) * 255.0);

                intensity = Math.max(
                        0,
                        Math.min(255, intensity));

                int argb = 0xFF000000 |
                        (intensity << 16) |
                        (intensity << 8) |
                        intensity;

                writer.setArgb(x, y, argb);
            }
        }
        // for (int y = 0; y < height; y++) {
        // for (int x = 0; x < width; x++) {
        //
        // double value = maxMagnitude > 0
        // ? magnitude[y][x] / maxMagnitude
        // : 0;
        //
        // output.getPixelWriter().setColor(
        // x, y,
        // Color.gray(value));
        // }
        // }

        return result;
    }

    public static Image aplicarDFT(Image imagem) {

        if (imagem == null) {
            throw new IllegalArgumentException("Imagem nula");
        }

        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader reader = imagem.getPixelReader();

        // Matrizes para armazenar os valores complexos
        double[][] real = new double[altura][largura];
        double[][] imaginario = new double[altura][largura];

        // 1. Converter a imagem para escala de cinza
        double[][] pixels = new double[altura][largura];

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {

                Color cor = reader.getColor(x, y);

                pixels[y][x] = 0.299 * cor.getRed()
                        + 0.587 * cor.getGreen()
                        + 0.114 * cor.getBlue();
            }
        }

        // 2. Aplicar a DFT bidimensional original
        for (int v = 0; v < altura; v++) {
            for (int u = 0; u < largura; u++) {

                double somaReal = 0.0;
                double somaImaginaria = 0.0;

                for (int y = 0; y < altura; y++) {
                    for (int x = 0; x < largura; x++) {

                        double angulo = 2.0 * Math.PI *
                                ((double) u * x / largura +
                                        (double) v * y / altura);

                        somaReal += pixels[y][x] * Math.cos(angulo);

                        somaImaginaria -= pixels[y][x] * Math.sin(angulo);
                    }
                }

                real[v][u] = somaReal;
                imaginario[v][u] = somaImaginaria;
            }
        }

        // 3. Calcular o espectro de magnitude
        double[][] magnitude = new double[altura][largura];

        double maiorMagnitude = 0.0;

        for (int v = 0; v < altura; v++) {
            for (int u = 0; u < largura; u++) {

                double mag = Math.sqrt(
                        real[v][u] * real[v][u] +
                                imaginario[v][u] * imaginario[v][u]);

                // Escala logarítmica para visualizar frequências
                magnitude[v][u] = Math.log1p(mag);

                maiorMagnitude = Math.max(
                        maiorMagnitude,
                        magnitude[v][u]);
            }
        }

        // 4. Criar imagem de saída com o espectro centralizado
        WritableImage resultado = new WritableImage(
                largura, altura);

        PixelWriter writer = resultado.getPixelWriter();

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {

                // Deslocar as baixas frequências para o centro
                int origemX = (x + (largura + 1) / 2) % largura;
                int origemY = (y + (altura + 1) / 2) % altura;

                double valor = magnitude[origemY][origemX];

                double normalizado = maiorMagnitude > 0
                        ? valor / maiorMagnitude
                        : 0.0;

                writer.setColor(
                        x, y,
                        Color.gray(normalizado));
            }
        }

        return resultado;
    }
}
