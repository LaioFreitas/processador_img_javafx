package br.image.utils;

import java.nio.ByteBuffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class HuffmanCodec {

    private NodeTrie trie;

    public HuffmanCodec() {
    }

    public HuffmanCodec(NodeTrie trie) {
        this.trie = trie;
    }

    public NodeTrie crieNo(Image image) {

        tranformation(image);

        return null;
    }

    private Image tranformation(Image image) {

        PixelReader readerImg = image.getPixelReader();
        WritableImage imgAux = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter writerImgAux = imgAux.getPixelWriter();
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        // ByteBuffer buffer = ByteBuffer.allocate(width * height);
        int[] pixels = new int[width];

        for (int i = 0; i < height; i++) {
            readerImg.getPixels(0, i, width, 1,
                    PixelFormat.getIntArgbInstance(), pixels, 0, width);

            for (int j = 0; i < width; j += 4) {
                if (j == 0) {
                    continue;
                }

                int pixelAnt = pixels[j - 1];

                int redAnt = (pixelAnt >> 16) & 0xff;
                int greenAnt = (pixelAnt >> 8) & 0xff;
                int blueAnt = (pixelAnt) & 0xff;

                int red = (pixels[j] >> 16) & 0xff;
                int green = (pixels[j] >> 8) & 0xff;
                int blue = pixels[j] & 0xff;

                red = redAnt - red;
                green = greenAnt - green;
                blue = blueAnt - blue;

                pixels[j] = (255 << 24) | (red << 16) | (green << 8) | blue;
            }

            writerImgAux.setPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        }
        return imgAux;
    }
}
