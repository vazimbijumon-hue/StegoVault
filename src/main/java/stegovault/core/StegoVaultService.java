package stegovault.core;

import stegovault.models.PayloadDetectionResult;
import stegovault.steganography.PayloadDecoder;
import stegovault.steganography.PayloadEncoder;

import javax.crypto.SecretKey;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * StegoVaultService provides the core steganography operations:
 * hiding data in images, extracting data from images, and detecting
 * whether an image contains a StegoVault payload.
 */
public final class StegoVaultService {

    private static final int SALT_LENGTH = 16;

    private final FileManager fileManager;
    private final PayloadEncoder payloadEncoder;
    private final PayloadDecoder payloadDecoder;
    private final SessionManager sessionManager;


    public StegoVaultService(
            SessionManager sessionManager
    ) {

        this.fileManager =
                new FileManager();

        this.payloadEncoder
                = new PayloadEncoder();

        this.payloadDecoder
                = new PayloadDecoder();

        this.sessionManager
                = sessionManager;
    }


    /**
     * Hides a file inside {@code image} with optional encryption.
     * If {@code password} is provided, the encryption key is derived from
     * the password together with a randomly generated salt, and the
     * {@link PayloadHeader#FLAG_ENCRYPTED} flag is set so that extraction
     * knows to obtain the salt from the embedded data.
     * If {@code password} is {@code null}, the existing session key is used
     * (backward-compatible mode, no salt appended to payload).
     *
     * @param file     the file to hide
     * @param image    the image to hide into
     * @param password optional user-supplied password for key derivation;
     *                 if {@code null} the existing session key is used
     * @return the image with the payload embedded
     * @throws IOException if the file cannot be read
     */
    public BufferedImage hideFile(
            Path file,
            BufferedImage image,
            String password
    ) throws IOException {

        Payload payload =
                fileManager.createPayload(file);

        return hidePayload(payload, image, password);
    }



    /**
     * Encrypts and embeds an already-constructed payload into {@code image}.
     * Unlike {@link #hideFile}, this never touches disk, so callers can
     * supply payloads that don't originate from a file (e.g. typed text).
     * If {@code password} is provided, the encryption key is derived from
     * the password together with a randomly generated salt, and the
     * {@link PayloadHeader#FLAG_ENCRYPTED} flag is set so that extraction
     * knows to obtain the salt from the embedded data.
     *
     * @param payload  the payload to embed (metadata + unencrypted data)
     * @param image    the image to embed into
     * @param password optional user-supplied password for key derivation;
     *                 if {@code null} the existing session key is used
     * @return the image with the payload embedded
     */
    public BufferedImage hidePayload(
            Payload payload,
            BufferedImage image,
            String password
    ) {

        byte[] payloadData = payload.data();
        byte[] encrypted;
        int flags;

        if (password != null && !password.isEmpty()) {

            // Derive key from user password + randomly generated salt
            byte[] salt = KeyManager.generateSalt();
            SecretKey key = KeyManager.deriveKeyFromPassword(password, salt);

            encrypted =
                    CryptoManager.encrypt(
                    payload.data(),
                    key
                    );

            // Append salt to the encrypted result: [IV(12)][ciphertext][salt(16)]
            byte[] encryptedWithSalt =
                    new byte[encrypted.length + SALT_LENGTH];

            System.arraycopy(
                    encrypted,
                    0,
                    encryptedWithSalt,
                    0,
                    encrypted.length
            );

            // Copy salt to the end
            System.arraycopy(
                    salt,
                    0,
                    encryptedWithSalt,
                    encrypted.length,
                    SALT_LENGTH
            );

            payloadData = encryptedWithSalt;
            flags = PayloadHeader.FLAG_ENCRYPTED;

        } else {

            // Existing behaviour: use session key, no salt, flags = 0
            SecretKey key = sessionManager.getKey();

            encrypted =
                    CryptoManager.encrypt(
                    payload.data(),
                    key
                    );

            // No salt appended; payload stays [IV(12)][ciphertext]
            payloadData = encrypted;
            flags = 0;
        }

        Payload encryptedPayload =
                new Payload(
                        payload.metadata(),
                        payloadData
                );

        // Set the encryption flag in the header via the encoder
        return payloadEncoder.encode(
                image,
                encryptedPayload,
                flags
        );
    }


    /**
     * Decodes and decrypts the payload hidden in {@code image} without
     * writing it to disk, so callers can inspect metadata (e.g. the
     * original file name) before choosing where to save it.
     *
     * @param password optional user-supplied password for key derivation;
     *                 if {@code null} the existing session key is used
     *                 for backward-compatible payloads (those without the
     *                 {@link PayloadHeader#FLAG_ENCRYPTED} flag)
     * @return the decrypted payload containing the original file data
     */
    public Payload extractPayload(
            BufferedImage image,
            String password
    ) {

        // Decode the payload and obtain the header so we can check flags
        byte[][] result = payloadDecoder.decodeWithHeader(image);
        byte[] serializedPayload = result[1];
        PayloadHeader header = PayloadHeader.deserialize(result[0]);

        Payload encryptedPayload = PayloadSerializer.deserialize(serializedPayload);

        boolean isEncrypted = header.flags() == PayloadHeader.FLAG_ENCRYPTED;

        byte[] decrypted;

        if (isEncrypted) {

            // New payload format: salt is embedded at the end of the encrypted data
            byte[] encryptedData = encryptedPayload.data();

            if (password == null || password.isEmpty()) {

                throw new IllegalArgumentException(
                        "Password required to decrypt encrypted payload. "
                        + "Provide the password used during hiding."
                );
            }

            // Extract the 16-byte salt from the end of the encrypted data
            byte[] salt = Arrays.copyOfRange(
                    encryptedData,
                    encryptedData.length - SALT_LENGTH,
                    encryptedData.length
            );

            // Derive the key from the user password + extracted salt
            SecretKey key = KeyManager.deriveKeyFromPassword(password, salt);

            // Trim the salt from the data so CryptoManager.decrypt receives
            // only [IV(12)][ciphertext] (without the trailing salt)
            byte[] encryptedTrimmed =
                    Arrays.copyOfRange(encryptedData, 0, encryptedData.length - SALT_LENGTH);

            decrypted =
                    CryptoManager.decrypt(
                            encryptedTrimmed,
                            key
                    );

        } else {

            // Backward-compatible payload: use the existing session key
            // (derived from the hardcoded "temporary" password in Main.java)
            SecretKey key = sessionManager.getKey();

            // The payload data is [IV(12)][ciphertext] with no salt appended
            decrypted =
                    CryptoManager.decrypt(
                            encryptedPayload.data(),
                            key
                    );
        }

        return new Payload(
                encryptedPayload.metadata(),
                decrypted
        );
    }


    /**
     * Extracts the hidden payload from {@code image} and saves it to {@code output}.
     *
     * @param image the image containing the hidden payload
     * @param output the path where the extracted file will be saved
     * @throws IOException if an I/O error occurs
     */
    public void extractFile(
            BufferedImage image,
            Path output
    ) throws IOException {

        Payload payload = extractPayload(image, null);

        fileManager.savePayload(
                payload,
                output
        );
    }


    /**
     * Checks whether {@code image} contains a StegoVault payload, without
     * decrypting it. This deliberately stops at {@link PayloadDecoder},
     * which already verifies the SHA-256 header checksum, so it never
     * needs the active session key and is safe to run on any PNG a user
     * picks for analysis.
     */
    public PayloadDetectionResult detectPayload(
            BufferedImage image
    ) {

        try {

            Payload encryptedPayload =
                    payloadDecoder.decode(image);

            return PayloadDetectionResult.found(
                    encryptedPayload.metadata().fileName(),
                    encryptedPayload.metadata().mimeType(),
                    encryptedPayload.size()
            );

        } catch (RuntimeException e) {

            String reason = e.getMessage() != null
                    ? e.getMessage()
                    : "No StegoVault payload detected.";

            return PayloadDetectionResult.notFound(reason);
        }
    }
}