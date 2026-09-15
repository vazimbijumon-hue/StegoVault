package stegovault.core;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import javax.crypto.spec.SecretKeySpec;

public final class KeyManager {


    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final int KEY_LENGTH =
            256;

    private static final int ITERATIONS =
            120000;

    private static final int SALT_LENGTH =
            16;


    private KeyManager() {
        // Utility class
    }


    /** Exposes the KDF algorithm name for read-only display (e.g. in Settings). */
    public static String algorithm() {
        return ALGORITHM;
    }


    /** Exposes the derived key length in bits for read-only display. */
    public static int keyLengthBits() {
        return KEY_LENGTH;
    }


    /** Exposes the PBKDF2 iteration count for read-only display. */
    public static int iterations() {
        return ITERATIONS;
    }
public static SecretKey generateSessionKey() {
    byte[] keyBytes = new byte[32];
    new SecureRandom().nextBytes(keyBytes);

    return new SecretKeySpec(keyBytes, "AES");
}


    public static byte[] generateSalt() {

        byte[] salt =
                new byte[SALT_LENGTH];


        SecureRandom random =
                new SecureRandom();


        random.nextBytes(salt);


        return salt;
    }

public static SecretKey deriveKey(
            String password,
            byte[] salt
    ) {

        try {

            PBEKeySpec spec =
                    new PBEKeySpec(
                            password.toCharArray(),
                            salt,
                            ITERATIONS,
                            KEY_LENGTH
                    );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            ALGORITHM
                    );

            byte[] keyBytes =
                    factory.generateSecret(spec)
                            .getEncoded();

            return new SecretKeySpec(
                    keyBytes,
                    "AES"

            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to derive encryption key.",
                    e
            );
        }
    }


    public static SecretKey deriveKeyFromPassword(
            String password,
            byte[] salt
    ) {

        return deriveKey(password, salt);
    }


    public static KeyMaterial generateKeyMaterial(
            String password
    ) {

        byte[] salt =
                generateSalt();

        SecretKey key =
                deriveKey(
                        password,
                        salt
                );

        return new KeyMaterial(
                key,
                salt
        );
    }
}