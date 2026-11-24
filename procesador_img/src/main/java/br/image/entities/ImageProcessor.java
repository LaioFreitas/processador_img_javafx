package br.image.entities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;

public class ImageProcessor extends ImageHandle {

    private IntensityTransformer intensityTransformer;
    private GeometricTransformer geometricTransformer;
    private EspacialProcessor espacialProcessor;
    protected boolean isGrayScale;

    public ImageProcessor() {
        super();
    }

    public ImageProcessor(Image image) {
        ImageHandle.image = image;
    }

    public EspacialProcessor getEspacialProcessor() {
        return espacialProcessor;
    }

    public void setEspacialProcessor(EspacialProcessor espacialProcessor) {
        this.espacialProcessor = espacialProcessor;
    }

    private void checkItIsgray() {

    }

    // public boolean isGrayScale() {
    // int height = (int) image.getHeight();
    // int width = (int) image.getWidth();
    // PixelReader reader = image.getPixelReader();
    // long numPixelEqual = 0;
    // int[] pixels = new int[width];
    // for (int i = 0; i < height; i++) {
    // reader.getPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(), pixels, 0,
    // width);
    // for (int j = 0; j < width; j++) {
    // int pixel = pixels[j];
    //
    // int red = (pixel >> 16) & 0xff;
    // int green = (pixel >> 8) & 0xff;
    // int blue = pixel & 0xff;
    //
    // if (red == green && red == blue && green == blue) {
    // numPixelEqual++;
    // }
    // }
    // }
    //
    // return numPixelEqual == width * height;
    // }
    //
    public IntensityTransformer getIntensityTransformer() {
        return this.intensityTransformer;
    }

    public void setIntensityTransformer(IntensityTransformer intensityTransformer) {
        this.intensityTransformer = intensityTransformer;
    }

    public GeometricTransformer getGeometricTransformer() {
        return geometricTransformer;
    }

    public void setGeometricTransformer(GeometricTransformer transformer) {
        this.geometricTransformer = transformer;
    }

}
