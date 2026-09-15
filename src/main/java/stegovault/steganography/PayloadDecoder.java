package stegovault.steganography;

import stegovault.core.Payload;
import stegovault.core.PayloadSerializer;

import java.awt.image.BufferedImage;
import java.util.Objects;

public final class PayloadDecoder {

    private final LSBdecoder lsbDecoder;


    public PayloadDecoder() {
        this.lsbDecoder = new LSBdecoder();
    }


    public Payload decode(BufferedImage image) {

        Objects.requireNonNull(
                image,
                "image"
        );


        byte[] serializedPayload =
                lsbDecoder.decode(image);


        return PayloadSerializer.deserialize(
                serializedPayload
        );
    }


    public byte[][] decodeWithHeader(BufferedImage image) {

        Objects.requireNonNull(
                image,
                "image"
        );


        return lsbDecoder.decodeWithHeader(image);
    }
}