package br.image.entities;

import br.image.exceptions.ImageNullException;
import br.image.gui.Alerts;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert;

public class IntensityTransformer extends ImageHandle {

    private Image imageOriginal;

    private float lastGamma;
    private int[] LUTGammaCorrection;

    private int lastBrightness;
    private int[] LUTBrightness;

    public IntensityTransformer() {
        super();
        lastGamma = 0.f;
        lastBrightness = 0;
        LUTBrightness = new int[256];
        LUTGammaCorrection = new int[256];
    }

    public IntensityTransformer(Image image) {
        super(image);
        lastGamma = 0.f;
        lastBrightness = 0;
        LUTBrightness = new int[256];
        LUTGammaCorrection = new int[256];
    }

    public Image Negative() {
        if (image == null) {
            throw new ImageNullException();
        }

        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader readerImg = image.getPixelReader();
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();
        int widthImg = (int) image.getWidth();
        int heightImg = (int) image.getHeight();
        int[] pixels = new int[widthImg];

        for (int i = 0; i < heightImg; i++) {
            readerImg.getPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
            for (int j = 0; j < widthImg; j++) {

                int alfa = (pixels[j] >> 24) & 0xff;
                int red = (pixels[j] >> 16) & 0xff;
                int green = (pixels[j] >> 8) & 0xff;
                int blue = pixels[j] & 0xff;

                int negativeR = 255 - red;
                int negativeG = 255 - green;
                int negativeB = 255 - blue;

                pixels[j] = (alfa << 24) | (negativeR << 16) | (negativeG << 8) | negativeB;
            }
            writerImgProc.setPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
        }
        image = imageProcessed;
        return imageProcessed;
    }

    public Image gammaCorrection(float gamma) {
        if (image == null) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada para correcao de gama nao é valida",
                    Alert.AlertType.ERROR);
        }
        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader readerImg = imageOriginal.getPixelReader();
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();
        int widthImg = (int) image.getWidth();
        int heightImg = (int) image.getHeight();
        int[] pixels = new int[widthImg];

        if (lastGamma != gamma) {
            lastGamma = gamma;
            for (int i = 0; i < 256; i++) {
                float result = (float) Math.pow(i / 255.f, gamma) * 255;
                if (result > 255)
                    result = 255;
                else if (result < 0)
                    result = 0;
                LUTGammaCorrection[i] = Math.round(result);
            }
            for (int i = 0; i < heightImg; i++) {
                readerImg.getPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);

                for (int j = 0; j < widthImg; j++) {

                    int alfa = (pixels[j] >> 24) & 0xff;
                    int red = (pixels[j] >> 16) & 0xff;
                    int green = (pixels[j] >> 8) & 0xff;
                    int blue = pixels[j] & 0xff;

                    int gammaCorrectedR = LUTGammaCorrection[red];
                    int gammaCorrectedG = LUTGammaCorrection[green];
                    int gammaCorrectedB = LUTGammaCorrection[blue];

                    pixels[j] = (alfa << 24) | (gammaCorrectedR << 16) | (gammaCorrectedG << 8) | (gammaCorrectedB);
                }
                writerImgProc.setPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
            }
        }
        image = imageProcessed;
        return imageProcessed;
    }

    public Image thresHolding(int thresHold) {
        if (image == null) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada nao é valida",
                    Alert.AlertType.ERROR);
        }

        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader readerImg = image.getPixelReader();
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();
        int widthImg = (int) image.getWidth();
        int heightImg = (int) image.getHeight();
        int[] pixels = new int[widthImg];

        for (int i = 0; i < heightImg; i++) {
            readerImg.getPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
            for (int j = 0; j < widthImg; j++) {

                int alfa = (pixels[j] >> 24) & 0xff;
                int red = (pixels[j] >> 16) & 0xff;
                int green = (pixels[j] >> 8) & 0xff;
                int blue = pixels[j] & 0xff;

                int thresHoldR = (red > thresHold) ? 255 : 0;
                int thresHoldG = (green > thresHold) ? 255 : 0;
                int thresHoldB = (blue > thresHold) ? 255 : 0;

                pixels[j] = (alfa << 24) | (thresHoldR << 16) | (thresHoldG << 8) | thresHoldB;
            }
            writerImgProc.setPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
        }
        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    public Image brightnessAdj(int brightness) {
        if (image == null) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada nao é valida",
                    Alert.AlertType.ERROR);
        }

        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        if (lastBrightness != brightness) {
            lastBrightness = brightness;

            for (int i = 0; i < 256; i++) {
                int result = i + brightness;
                if (result > 255)
                    result = 255;
                else if (result < 0)
                    result = 0;

                LUTBrightness[i] = result;
            }
            PixelReader readerImg = imageOriginal.getPixelReader();
            PixelWriter writerImgProc = imageProcessed.getPixelWriter();
            int widthImg = (int) image.getWidth();
            int heightImg = (int) image.getHeight();
            int[] pixels = new int[widthImg];

            for (int i = 0; i < heightImg; i++) {
                readerImg.getPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
                for (int j = 0; j < widthImg; j++) {

                    int alfa = (pixels[j] >> 24) & 0xff;
                    int red = (pixels[j] >> 16) & 0xff;
                    int green = (pixels[j] >> 8) & 0xff;
                    int blue = pixels[j] & 0xff;

                    red = LUTBrightness[red];
                    green = LUTBrightness[green];
                    blue = LUTBrightness[blue];

                    pixels[j] = (alfa << 24) | (red << 16) | (green << 8) | blue;
                }
                writerImgProc.setPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
            }
            image = imageProcessed;
        }
        return imageProcessed;
    }

    public Image toGrayScale() {
        if (image == null) {
            Alerts.showAlert("Error", "Imagem nula", "Imagem selecionada nao é valida",
                    Alert.AlertType.ERROR);
        }

        WritableImage imageProcessed = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter writerImgProc = imageProcessed.getPixelWriter();
        PixelReader readerImg = image.getPixelReader();
        int widthImg = (int) image.getWidth();
        int heightImg = (int) image.getHeight();
        int[] pixels = new int[widthImg];

        for (int i = 0; i < heightImg; i++) {
            readerImg.getPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
            for (int j = 0; j < widthImg; j++) {
                int pixel = pixels[j];

                int alfa = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int gray = (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);

                pixels[j] = (alfa << 24) | (gray << 16) | (gray << 8) | gray;
            }
            writerImgProc.setPixels(0, i, widthImg, 1, PixelFormat.getIntArgbInstance(), pixels, 0, widthImg);
        }
        image = imageProcessed;
        return imageProcessed;
    }

    public Image getImageOriginal() {
        return imageOriginal;
    }

    public void setImageOriginal(Image imageOriginal) {
        this.imageOriginal = imageOriginal;
    }
}
