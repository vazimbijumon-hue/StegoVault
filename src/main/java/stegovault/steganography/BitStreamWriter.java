package stegovault.steganography;

public class BitStreamWriter {

    private final byte[] data;
    private int byteIndex;
    private int bitIndex;


    public BitStreamWriter(byte[] data) {

        this.data = data;
        this.byteIndex = 0;
        this.bitIndex = 0;
    }


    public int nextBit() {

        int bit = (data[byteIndex] >> (7 - bitIndex)) & 1;

        bitIndex++;

        if (bitIndex == 8) {
            bitIndex = 0;
            byteIndex++;
        }

        return bit;
    }


    public boolean hasNextBit() {
        return byteIndex < data.length;
    }


    public boolean hasNextBit(int count) {

        int remainingBits = ((data.length - byteIndex) * 8) - bitIndex;

        return remainingBits >= count;
    }
}