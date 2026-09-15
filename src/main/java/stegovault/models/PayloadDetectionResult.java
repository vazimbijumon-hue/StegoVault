package stegovault.models;

/**
 * Result of checking whether an image contains a StegoVault payload.
 *
 * This is a read-only inspection result: it never triggers decryption,
 * so it can be produced for any image regardless of the active session
 * key. If a payload is detected, its header checksum has already been
 * verified (see PayloadDecoder / PayloadHeader), so a "found" result
 * always implies the embedded data passed SHA-256 integrity checking.
 */
public final class PayloadDetectionResult {

    private final boolean payloadDetected;
    private final String fileName;
    private final String mimeType;
    private final long payloadSizeBytes;
    private final String message;

    private PayloadDetectionResult(
            boolean payloadDetected,
            String fileName,
            String mimeType,
            long payloadSizeBytes,
            String message
    ) {
        this.payloadDetected = payloadDetected;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.payloadSizeBytes = payloadSizeBytes;
        this.message = message;
    }

    public static PayloadDetectionResult found(
            String fileName,
            String mimeType,
            long payloadSizeBytes
    ) {
        return new PayloadDetectionResult(
                true,
                fileName,
                mimeType,
                payloadSizeBytes,
                "StegoVault payload detected. SHA-256 integrity check passed."
        );
    }

    public static PayloadDetectionResult notFound(String reason) {
        return new PayloadDetectionResult(
                false,
                null,
                null,
                0,
                reason
        );
    }

    public boolean isPayloadDetected() {
        return payloadDetected;
    }

    public String fileName() {
        return fileName;
    }

    public String mimeType() {
        return mimeType;
    }

    public long payloadSizeBytes() {
        return payloadSizeBytes;
    }

    public String message() {
        return message;
    }
}
