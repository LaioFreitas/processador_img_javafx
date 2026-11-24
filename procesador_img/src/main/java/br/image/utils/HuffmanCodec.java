package br.image.utils;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class HuffmanCodec {

    private NodeTrie trieR;
    private NodeTrie trieG;
    private NodeTrie trieB;

    public HuffmanCodec() {
    }

    public HuffmanCodec(NodeTrie trieR, NodeTrie trieG, NodeTrie trieB) {
        this.trieR = trieR;
        this.trieG = trieG;
        this.trieB = trieB;
    }

    public NodeTrie compress(Image image) {

        int[][] imageDPCM = tranformation(image);

        trieR = createTrie(imageDPCM[0]);
        trieG = createTrie(imageDPCM[1]);
        trieB = createTrie(imageDPCM[2]);

        return null;
    }

    private static NodeTrie createTrie(int[] intencityDPCM) {
        int frequency[] = new int[256];

        for (int e : intencityDPCM) {
            ++frequency[e];
        }

        PriorityQueue<NodeTrie> tries = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (frequency[i] > 0) {
                tries.add(new NodeTrie(i, frequency[i]));
            }
        }

        while (tries.size() > 1) {
            NodeTrie t1 = tries.remove();
            NodeTrie t2 = tries.remove();

            tries.add(new NodeTrie(t1.getFrequency() + t2.getFrequency(), t1, t2));
        }

        return tries.remove();
    }

    // private Image tranformation(Image image) {
    //
    // PixelReader readerImg = image.getPixelReader();
    // WritableImage imgAux = new WritableImage((int) image.getWidth(), (int)
    // image.getHeight());
    // PixelWriter writerImgAux = imgAux.getPixelWriter();
    // int height = (int) image.getHeight();
    // int width = (int) image.getWidth();
    // // ByteBuffer buffer = ByteBuffer.allocate(width * height);
    // int[] pixels = new int[width];
    //
    // for (int i = 0; i < height; i++) {
    // readerImg.getPixels(0, i, width, 1,
    // PixelFormat.getIntArgbInstance(), pixels, 0, width);
    //
    // for (int j = 0; i < width; j += 4) {
    // if (j == 0) {
    // continue;
    // }
    //
    // int pixelAnt = pixels[j - 1];
    //
    // int redAnt = (pixelAnt >> 16) & 0xff;
    // int greenAnt = (pixelAnt >> 8) & 0xff;
    // int blueAnt = (pixelAnt) & 0xff;
    //
    // int red = (pixels[j] >> 16) & 0xff;
    // int green = (pixels[j] >> 8) & 0xff;
    // int blue = pixels[j] & 0xff;
    //
    // red = redAnt - red;
    // green = greenAnt - green;
    // blue = blueAnt - blue;
    //
    // pixels[j] = (255 << 24) | (red << 16) | (green << 8) | blue;
    // }
    //
    // writerImgAux.setPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(),
    // pixels, 0, width);
    // }
    // return imgAux;
    // }

    private int[][] tranformation(Image image) {

        PixelReader readerImg = image.getPixelReader();
        WritableImage imgAux = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter writerImgAux = imgAux.getPixelWriter();
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        // ByteBuffer buffer = ByteBuffer.allocate(width * height);
        int[] pixels = new int[width];
        int[][] pixelsDPCM = new int[3][width * height];

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
                pixelsDPCM[0][i * j] = red;
                pixelsDPCM[1][i * j] = green;
                pixelsDPCM[2][i * j] = blue;

                pixels[j] = (255 << 24) | (red << 16) | (green << 8) | blue;
            }

            writerImgAux.setPixels(0, i, width, 1, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        }
        return pixelsDPCM;
    }

}
