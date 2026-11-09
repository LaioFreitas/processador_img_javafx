package br.image.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class BrushTool {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private double size = 10;

    private double lastX;
    private double lastY;

    private boolean drawing = false;
    private boolean eraser = false;

    public BrushTool(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        configureEvents();
    }

    private void configureEvents() {

        canvas.setOnMousePressed(event -> {

            if (event.getButton() != MouseButton.PRIMARY)
                return;

            drawing = true;

            lastX = event.getX();
            lastY = event.getY();

            gc.save();

            gc.setLineWidth(size);

            if (eraser) {
                gc.clearRect(
                        lastX - size / 2,
                        lastY - size / 2,
                        size,
                        size);

            } else {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(size);
                gc.strokeLine(
                        lastX,
                        lastY,
                        lastX,
                        lastY);
            }

            // Desenha um ponto inicial
            gc.strokeLine(lastX, lastY, lastX, lastY);

            gc.restore();
        });

        canvas.setOnMouseDragged(event -> {

            if (!drawing)
                return;

            double x = event.getX();
            double y = event.getY();

            gc.save();

            gc.setLineWidth(size);

            if (eraser) {
                gc.clearRect(
                        lastX - size / 2,
                        lastY - size / 2,
                        size,
                        size);
            } else {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(size);

                gc.strokeLine(
                        lastX,
                        lastY,
                        lastX,
                        lastY);
            }

            gc.strokeLine(lastX, lastY, x, y);

            gc.restore();

            lastX = x;
            lastY = y;
        });

        canvas.setOnMouseReleased(event -> {
            drawing = false;
        });
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setEraser(boolean eraser) {
        this.eraser = eraser;
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
