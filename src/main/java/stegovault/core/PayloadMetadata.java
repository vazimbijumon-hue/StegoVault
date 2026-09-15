package stegovault.core;

import java.util.Objects;

/**
 * Stores information about the original payload.
 *
 * PayloadMetadata describes the hidden content,
 * but does not contain the actual payload bytes.
 */
public final class PayloadMetadata {

    private final String fileName;
    private final String mimeType;


    public PayloadMetadata(String fileName, String mimeType) {

        this.fileName = Objects.requireNonNull(
                fileName,
                "fileName"
        );

        this.mimeType = Objects.requireNonNull(
                mimeType,
                "mimeType"
        );


        if (fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be empty."
            );
        }

        if (mimeType.isBlank()) {
            throw new IllegalArgumentException(
                    "MIME type cannot be empty."
            );
        }
    }


    public String fileName() {
        return fileName;
    }


    public String mimeType() {
        return mimeType;
    }
}