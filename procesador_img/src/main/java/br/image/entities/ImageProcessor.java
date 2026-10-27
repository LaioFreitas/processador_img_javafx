package br.image.entities;

import javafx.scene.image.Image;

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

    public boolean isGrayScale() {
        return isGrayScale;
    }

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
