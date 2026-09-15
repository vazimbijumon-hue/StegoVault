package stegovault.core;

import stegovault.steganography.BitStreamWriter;

public class BitStreamWriterTest {

    public static void main(String[] args) {

        byte[] message = "hiiii".getBytes();

        BitStreamWriter writer = new BitStreamWriter(message);

        for (int i = 0; i < 8; i++) {

            System.out.println(writer.nextBit());

        }

    }

}