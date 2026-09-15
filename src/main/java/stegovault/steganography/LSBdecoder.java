package stegovault.steganography;

import java.awt.image.BufferedImage;
import stegovault.core.PayloadHeader;
import stegovault.exception.InvalidPayloadException;

public class LSBdecoder {

    private int extractBit(int colourValue) {
        return colourValue & 1;
    }

    private int[] extractPixelBits(int rgb) {

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        return new int[]{
                extractBit(red),
                extractBit(green),
                extractBit(blue)
        };
    }

    private byte[] readBytes(BufferedImage image, int startBit, int byteCount) {

        BitStreamReader reader = new BitStreamReader();

        int width = image.getWidth();
        int height = image.getHeight();

        int bitsNeeded = byteCount * 8;
        int bitsRead = 0;
        int currentBit = 0;

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int rgb = image.getRGB(x, y);

                int[] bits = extractPixelBits(rgb);

                for (int bit : bits) {

                    if (currentBit < startBit) {
                        currentBit++;
                        continue;
                    }

                    if (bitsRead == bitsNeeded) {
                        return reader.toByteArray();
                    }

                    reader.addBit(bit);
                    bitsRead++;
                    currentBit++;
                }
            }
        }

        return reader.toByteArray();
    }


    public byte[] decode(BufferedImage image) {

        try {

            byte[] headerBytes = readBytes(
                    image,
                    0,
                    PayloadHeader.SERIALIZED_LENGTH
            );


            PayloadHeader header =
                    PayloadHeader.deserialize(headerBytes);


            byte[] payload = readBytes(
                    image,
                    PayloadHeader.SERIALIZED_LENGTH * 8,
                    header.payloadLength()
            );


            if (!header.matchesPayload(payload)) {

                throw new InvalidPayloadException(
                        "Payload checksum verification failed."
                );


            }


            return payload;


        } catch (InvalidPayloadException e) {


            throw e;


        } catch (Exception e) {


            throw new InvalidPayloadException(
                    "Failed to decode StegoVault payload.",
                    e
            );
        }
    }


    public byte[][] decodeWithHeader(BufferedImage image) {

        try {

            byte[] headerBytes = readBytes(
                    image,
                    0,
                    PayloadHeader.SERIALIZED_LENGTH
            );


            PayloadHeader header =
                    PayloadHeader.deserialize(headerBytes);


            byte[] payload = readBytes(
                    image,
                    PayloadHeader.SERIALIZED_LENGTH * 8,
                    header.payloadLength()
            );


            if (!header.matchesPayload(payload)) {

                throw new InvalidPayloadException(
                        "Payload checksum verification failed."
                );


            }


            return new byte[][]{headerBytes, payload};


        } catch (InvalidPayloadException e) {


            throw e;


        } catch (Exception e) {


            throw new InvalidPayloadException(
                    "Failed to decode StegoVault payload.",
                    e
            );
        }
    }
}