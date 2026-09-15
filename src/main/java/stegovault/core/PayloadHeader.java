package stegovault.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

/**
 * Metadata stored before an embedded payload.
 *
 * The header identifies StegoVault data, stores payload size,
 * and verifies integrity using SHA-256.
 */
public final class PayloadHeader {

    public static final int FORMAT_VERSION = 1;
    public static final int FLAG_ENCRYPTED = 0x01;
    public static final int CHECKSUM_LENGTH = 32;

    public static final int SERIALIZED_LENGTH =
            4 + 1 + 1 + Integer.BYTES + CHECKSUM_LENGTH;

    private static final byte[] MAGIC = {'S', 'T', 'G', 'V'};

    private final int flags;
    private final int payloadLength;
    private final byte[] payloadChecksum;


    private PayloadHeader(
            int flags,
            int payloadLength,
            byte[] payloadChecksum
    ) {

        if (flags < 0 || flags > 0xFF) {
            throw new IllegalArgumentException(
                    "Flags must fit in one byte."
            );
        }

        if (payloadLength < 0) {
            throw new IllegalArgumentException(
                    "Payload length cannot be negative."
            );
        }

        if (payloadChecksum.length != CHECKSUM_LENGTH) {
            throw new IllegalArgumentException(
                    "Payload checksum must be 32 bytes."
            );
        }

        this.flags = flags;
        this.payloadLength = payloadLength;
        this.payloadChecksum = payloadChecksum.clone();
    }


    public static PayloadHeader forPayload(
            int flags,
            byte[] payload
    ) {

        Objects.requireNonNull(payload, "payload");

        return new PayloadHeader(
                flags,
                payload.length,
                sha256(payload)
        );
    }


    public static PayloadHeader deserialize(byte[] bytes) {

        Objects.requireNonNull(bytes, "bytes");

        if (bytes.length != SERIALIZED_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid payload header length."
            );
        }

        ByteBuffer buffer =
                ByteBuffer.wrap(bytes)
                        .order(ByteOrder.BIG_ENDIAN);


        byte[] magic = new byte[MAGIC.length];
        buffer.get(magic);

        if (!Arrays.equals(MAGIC, magic)) {
            throw new IllegalArgumentException(
                    "Image does not contain a StegoVault payload."
            );
        }


        int version = Byte.toUnsignedInt(buffer.get());

        if (version != FORMAT_VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported payload format version: " + version
            );
        }


        int flags = Byte.toUnsignedInt(buffer.get());

        int payloadLength = buffer.getInt();

        byte[] checksum = new byte[CHECKSUM_LENGTH];

        buffer.get(checksum);


        return new PayloadHeader(
                flags,
                payloadLength,
                checksum
        );
    }


    public byte[] serialize() {

        return ByteBuffer.allocate(SERIALIZED_LENGTH)
                .order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC)
                .put((byte) FORMAT_VERSION)
                .put((byte) flags)
                .putInt(payloadLength)
                .put(payloadChecksum)
                .array();
    }


    public boolean matchesPayload(byte[] payload) {

        Objects.requireNonNull(payload, "payload");

        return payload.length == payloadLength
                &&
                MessageDigest.isEqual(
                        payloadChecksum,
                        sha256(payload)
                );
    }


    public int flags() {
        return flags;
    }


    public int payloadLength() {
        return payloadLength;
    }


    public byte[] payloadChecksum() {
        return payloadChecksum.clone();
    }


    private static byte[] sha256(byte[] input) {

        try {

            return MessageDigest
                    .getInstance("SHA-256")
                    .digest(input);

        } catch (java.security.NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 must be available in the Java runtime.",
                    exception
            );
        }
    }
}