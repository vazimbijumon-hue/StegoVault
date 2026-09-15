package stegovault.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import stegovault.exception.InvalidPayloadException;

public final class PayloadSerializer {
          private static final int MAX_PAYLOAD_SIZE = 100 * 1024 * 1024;

    private PayloadSerializer() {
        // Utility class
    }


    public static byte[] serialize(Payload payload) {

        Objects.requireNonNull(
                payload,
                "payload"
        );

        byte[] fileNameBytes =
                payload.metadata()
                        .fileName()
                        .getBytes(StandardCharsets.UTF_8);

        byte[] mimeTypeBytes =
                payload.metadata()
                        .mimeType()
                        .getBytes(StandardCharsets.UTF_8);

        byte[] data = payload.data();


        ByteBuffer buffer =
                ByteBuffer.allocate(
                                Integer.BYTES + fileNameBytes.length
                                        +
                                        Integer.BYTES + mimeTypeBytes.length
                                        +
                                        data.length
                        )
                        .order(ByteOrder.BIG_ENDIAN);


        buffer.putInt(fileNameBytes.length);
        buffer.put(fileNameBytes);

        buffer.putInt(mimeTypeBytes.length);
        buffer.put(mimeTypeBytes);

        buffer.put(data);


        return buffer.array();
    }


    public static Payload deserialize(byte[] bytes) {

    Objects.requireNonNull(
            bytes,
            "bytes"
    );

    try {

        ByteBuffer buffer =
                ByteBuffer.wrap(bytes)
                        .order(ByteOrder.BIG_ENDIAN);


        // File name length
        if (buffer.remaining() < Integer.BYTES) {
            throw new InvalidPayloadException(
                    "Invalid payload: missing filename length."
            );
        }

        int fileNameLength = buffer.getInt();

        if (fileNameLength < 0
                || fileNameLength > buffer.remaining()) {
            throw new InvalidPayloadException(
                    "Invalid payload: invalid filename length."
            );
        }

        byte[] fileNameBytes =
                new byte[fileNameLength];

        buffer.get(fileNameBytes);


        // MIME type length
        if (buffer.remaining() < Integer.BYTES) {
            throw new InvalidPayloadException(
                    "Invalid payload: missing MIME type length."
            );
        }

        int mimeTypeLength = buffer.getInt();

        if (mimeTypeLength < 0
                || mimeTypeLength > buffer.remaining()) {
            throw new InvalidPayloadException(
                    "Invalid payload: invalid MIME type length."
            );
        }

        byte[] mimeTypeBytes =
                new byte[mimeTypeLength];

        buffer.get(mimeTypeBytes);


        // Remaining bytes are the actual payload data
      int dataLength = buffer.remaining();

if (dataLength > MAX_PAYLOAD_SIZE) {
    throw new InvalidPayloadException(
            "Invalid payload: payload is too large."
    );
}

byte[] data =
        new byte[dataLength];

        buffer.get(data);


        PayloadMetadata metadata =
                new PayloadMetadata(
                        new String(
                                fileNameBytes,
                                StandardCharsets.UTF_8
                        ),
                        new String(
                                mimeTypeBytes,
                                StandardCharsets.UTF_8
                        )
                );


        return new Payload(
                metadata,
                data
        );


    } catch (InvalidPayloadException e) {

        throw e;

    } catch (Exception e) {

        throw new InvalidPayloadException(
                "Failed to decode payload.",
                e
        );
    }
}


    }
