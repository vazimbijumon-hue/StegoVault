package stegovault.steganography;

import stegovault.core.PayloadHeader;
import java.awt.image.BufferedImage;

public class LSBEncoder {

    private int embedBit(int colourValue, int bit) {
        return (colourValue & 0xFE) | bit;
    }

    private int embedPixel(int rgb, int bitR, int bitG, int bitB) {

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        red = embedBit(red, bitR);
        green = embedBit(green, bitG);
        blue = embedBit(blue, bitB);

        return (red << 16) | (green << 8) | blue;
    }

    public BufferedImage encode(BufferedImage image, byte[] message) {
        return encode(image, message, 0);
    }


    public BufferedImage encode(BufferedImage image, byte[] message, int flags) {

        PayloadHeader header = PayloadHeader.forPayload(flags, message);

        byte[] headerBytes = header.serialize();

        byte[] payload = new byte[headerBytes.length + message.length];

        System.arraycopy(headerBytes, 0, payload, 0, headerBytes.length);
        System.arraycopy(message, 0, payload, headerBytes.length, message.length);

        int width = image.getWidth();
        int height = image.getHeight();

        long requiredBits = (long) payload.length * 8;
        long availableBits = (long) width * height * 3;

        if (requiredBits > availableBits) {
            throw new IllegalArgumentException("Image does not have enough capacity.");
        }

        BitStreamWriter writer = new BitStreamWriter(payload);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                if (!writer.hasNextBit()) {
                    return image;
                }

                int rgb = image.getRGB(x, y);

                int bitR = writer.nextBit();
                int bitG = writer.hasNextBit() ? writer.nextBit() : 0;
                int bitB = writer.hasNextBit() ? writer.nextBit() : 0;

                int newRgb = embedPixel(rgb, bitR, bitG, bitB);

                image.setRGB(x, y, newRgb);
            }
        }

        return image;
    }
}