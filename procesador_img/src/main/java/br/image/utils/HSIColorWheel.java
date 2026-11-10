package br.image.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

public class HSIColorWheel extends Canvas {

    private double hue = 0;
    private double saturation = 0;
    private double intensity = 0.5;

    private double radius;
    private double centerX;
    private double centerY;

    private WritableImage wheelImage;

    private Consumer<HSIConverter.HSI> onSelectionChanged;

    public HSIColorWheel(double size) {
        super(size, size);

        radius = size / 2.0 - 10;
        centerX = size / 2.0;
        centerY = size / 2.0;

        generateWheel();
        draw();

        setOnMousePressed(this::handleMouse);
        setOnMouseDragged(this::handleMouse);
    }

    private void generateWheel() {

        int size = (int) getWidth();

        wheelImage = new WritableImage(size, size);

        PixelWriter writer = wheelImage.getPixelWriter();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                double dx = x - centerX;
                double dy = centerY - y;

                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= radius) {

                    double s = distance / radius;

                    double h = Math.toDegrees(
                            Math.atan2(dy, dx));

                    if (h < 0) {
                        h += 360;
                    }

                    Color color = hsiToRGB(h, s, intensity);

                    writer.setColor(x, y, color);

                } else {
                    writer.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
    }

    // Conversão inversa: HSI para RGB
    private Color hsiToRGB(double h, double s, double i) {
        return HSIConverter.hsiToRGB(new HSIConverter.HSI(h, s, i));
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    private void handleMouse(MouseEvent event) {

        double dx = event.getX() - centerX;
        double dy = centerY - event.getY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > radius) {
            return;
        }

        saturation = distance / radius;

        hue = Math.toDegrees(Math.atan2(dy, dx));

        if (hue < 0) {
            hue += 360;
        }

        draw();
        notifySelection();
    }

    private void draw() {

        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.drawImage(wheelImage, 0, 0);

        // Indicador de seleção
        double angle = Math.toRadians(hue);

        double x = centerX +
                Math.cos(angle) * saturation * radius;

        double y = centerY -
                Math.sin(angle) * saturation * radius;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeOval(x - 6, y - 6, 12, 12);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(x - 7, y - 7, 14, 14);
    }

    public void setIntensity(double intensity) {

        this.intensity = clamp(intensity);

        generateWheel();
        draw();

        notifySelection();
    }

    private void notifySelection() {

        if (onSelectionChanged != null) {

            onSelectionChanged.accept(
                    new HSIConverter.HSI(
                            hue,
                            saturation,
                            intensity));
        }
    }

    public void setOnSelectionChanged(
            Consumer<HSIConverter.HSI> callback) {
        this.onSelectionChanged = callback;
    }

    public double getHue() {
        return hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getIntensity() {
        return intensity;
    }
}
