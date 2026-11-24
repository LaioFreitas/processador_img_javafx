package br.image.entities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;

public abstract class ImageHandle {
    protected static Image image;
    // protected static Image imageOriginal;
    // protected static WritableImage imageProcessed;

    public ImageHandle() {
    }

    public ImageHandle(Image image) {
        ImageHandle.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image imageOriginal) {
        ImageHandle.image = imageOriginal;
    }

    public boolean isGrayScale() {
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        PixelReader reader = image.getPixelReader();
        long numPixelEqual = 0;
        int[] pixels = new int[width];
        for (int i = 0; i < height; i++) {
            reader.getPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(), pixels, 0, width);
            for (int j = 0; j < width; j++) {
                int pixel = pixels[j];

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                if (red == green && red == blue && green == blue) {
                    numPixelEqual++;
                }
            }
        }

        return numPixelEqual == width * height;
    }

    // public Image getImageOriginal() {
    // return imageOriginal;
    // }

    // public void setImageOriginal(Image imageOriginal) {
    // ImageHandle.imageOriginal = imageOriginal;
    // }

    // public WritableImage getImageProcessed() {
    // return imageProcessed;
    // }

    // public void setImageProcessed(WritableImage imageProcessed) {
    // ImageHandle.imageProcessed = imageProcessed;
    // }
    protected int extractA(int argb) {
        return (argb >> 24) & 0xff;
    }

    protected int extractR(int argb) {
        return (argb >> 16) & 0xff;
    }

    protected int extractG(int argb) {
        return (argb >> 8) & 0xff;
    }

    protected int extractB(int argb) {
        return argb & 0xff;
    }

    protected int lerpComponent(int c00, int c10, int c01, int c11, float dx, float dy) {
        float top = c00 * (1 - dx) + c10 * dx;
        float bottom = c01 * (1 - dx) + c11 * dx;
        float val = top * (1 - dy) + bottom * dy;
        return Math.min(255, Math.max(0, (int) val));
    }

}
