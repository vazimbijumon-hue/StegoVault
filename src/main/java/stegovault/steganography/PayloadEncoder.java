package stegovault.steganography;

import stegovault.core.Payload;
import stegovault.core.PayloadSerializer;

import java.awt.image.BufferedImage;
import java.util.Objects;

public final class PayloadEncoder {

    private final LSBEncoder lsbEncoder;


    public PayloadEncoder() {
        this.lsbEncoder = new LSBEncoder();
    }


    public BufferedImage encode(
            BufferedImage image,
            Payload payload
    ) {
        return encode(image, payload, 0);
    }


    public BufferedImage encode(
            BufferedImage image,
            Payload payload,
            int flags
    ) {

        Objects.requireNonNull(
                image,
                "image"
        );

        Objects.requireNonNull(
                payload,
                "payload"
        );


        byte[] serializedPayload =
                PayloadSerializer.serialize(payload);


        return lsbEncoder.encode(
                image,
                serializedPayload,
                flags
        );
    }
}