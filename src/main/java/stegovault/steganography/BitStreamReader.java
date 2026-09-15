package stegovault.steganography;

import java.io.ByteArrayOutputStream;

public class BitStreamReader {
    private final ByteArrayOutputStream buffer;
    private int currentByte;
    private int bitCount;

 public BitStreamReader() {

            buffer = new ByteArrayOutputStream();
            currentByte =0;
            bitCount=0;

 }
    public void addBit(int bit) {

        currentByte = (currentByte << 1) | bit;

        bitCount++;

        if (bitCount == 8) {

            buffer.write(currentByte);

            currentByte = 0;
            bitCount = 0;
        }
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }
 }



