package stegovault.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the complete payload hidden inside StegoVault.
 *
 * A Payload contains:
 * - Metadata describing the content
 * - The actual payload bytes
 *
 * It does not know about images, encryption, or storage.
 */
public final class Payload {

    private final PayloadMetadata metadata;
    private final byte[] data;


    public Payload(PayloadMetadata metadata, byte[] data) {

        this.metadata = Objects.requireNonNull(
                metadata,
                "metadata"
        );

        Objects.requireNonNull(
                data,
                "data"
        );

        if (data.length == 0) {
            throw new IllegalArgumentException(
                    "Payload data cannot be empty."
            );
        }

        this.data = data.clone();
    }


    public PayloadMetadata metadata() {
        return metadata;
    }


    public byte[] data() {
        return data.clone();
    }


    public int size() {
        return data.length;
    }


    @Override
    public String toString() {
        return "Payload{" +
                "metadata=" + metadata.fileName() +
                ", size=" + data.length +
                " bytes}";
    }
}