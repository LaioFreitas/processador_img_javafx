package br.image.entities;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

import java.util.Arrays;

import br.image.exceptions.ImageNullException;

public class GeometricTransformer extends ImageHandle {

    public Image scale(int sx, int sy) {
        if (image == null) {
            throw new ImageNullException();
        }

        WritableImage imageProcessed = new WritableImage((int) image.getWidth() * sx,
                (int) image.getHeight() * sy);

        PixelReader reader = image.getPixelReader();
        PixelWriter writer = imageProcessed.getPixelWriter();
        int origWidth = (int) image.getWidth();
        int origHeight = (int) image.getHeight();

        for (int i = 0; i < imageProcessed.getHeight(); i++) {
            for (int j = 0; j < imageProcessed.getWidth(); j++) {
                float gx = (float) j / (float) sx;
                float gy = (float) i / (float) sy;

                int x0 = (int) Math.floor(gx);
                int y0 = (int) Math.floor(gy);
                int x1 = Math.min(x0 + 1, origWidth - 1);
                int y1 = Math.min(y0 + 1, origHeight - 1);
                x0 = Math.max(0, x0);
                y0 = Math.max(0, y0);

                float dx = gx - x0;
                float dy = gy - y0;

                int p00 = reader.getArgb(x0, y0);
                int p10 = reader.getArgb(x1, y0);
                int p01 = reader.getArgb(x0, y1);
                int p11 = reader.getArgb(x1, y1);

                int a = lerpComponent(extractA(p00), extractA(p10), extractA(p01), extractA(p11), dx, dy);
                int r = lerpComponent(extractR(p00), extractR(p10), extractR(p01), extractR(p11), dx, dy);
                int g = lerpComponent(extractG(p00), extractG(p10), extractG(p01), extractG(p11), dx, dy);
                int b = lerpComponent(extractB(p00), extractB(p10), extractB(p11), extractB(p11), dx, dy);

                int finalArgb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(j, i, finalArgb);
            }
        }
        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }

    public Image rotation(int angle) {
        if (image == null) {
            throw new ImageNullException();
        }

        float ang = (float) (angle * Math.PI) / 180.f;

        int[][] verticiesPoints = {
                { 0, 0 },
                { 0, (int) image.getWidth() },
                { (int) image.getHeight(), 0 },
                { (int) image.getHeight(), (int) image.getWidth() }
        };

        double[][] rotationMatrix = {
                { Math.cos(ang), -Math.sin(ang) },
                { Math.sin(ang), Math.cos(ang) }
        };

        double[][] pointsRotated = new double[4][2];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++)
                    pointsRotated[i][j] += verticiesPoints[i][k] * rotationMatrix[k][j];
            }
        }

        double[] xCol = Arrays.stream(pointsRotated)
                .mapToDouble(col -> col[1])
                .toArray();

        double[] yCol = Arrays.stream(pointsRotated)
                .mapToDouble(col -> col[0])
                .toArray();

        double xMax = Arrays.stream(xCol)
                .max()
                .orElse(Double.NaN);

        double xMin = Arrays.stream(xCol)
                .min()
                .orElse(Double.NaN);

        double yMax = Arrays.stream(yCol)
                .max()
                .orElse(Double.NaN);

        double yMin = Arrays.stream(yCol)
                .min()
                .orElse(Double.NaN);

        WritableImage imageProcessed = new WritableImage(
                (int) Math.round(yMax - yMin),
                (int) Math.round(xMax - xMin));

        PixelReader reader = image.getPixelReader();
        PixelWriter writer = imageProcessed.getPixelWriter();

        for (int i = 0; i < imageProcessed.getHeight(); i++) {
            for (int j = 0; j < imageProcessed.getWidth(); j++) {
                int x = (int) Math.floor((j + xMin) * Math.cos(-ang) - (i + yMin) * Math.sin(-ang));
                int y = (int) Math.floor((j + xMin) * Math.sin(-ang) + (i + yMin) * Math.cos(-ang));

                if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
                    writer.setArgb(j, i, reader.getArgb(x, y));
                } else {
                    writer.setArgb(j, i, 0);
                }
            }
        }
        ImageHandle.image = imageProcessed;
        return imageProcessed;
    }
}
