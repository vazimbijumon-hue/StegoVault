package stegovault.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public final class CryptoManager {

    private static final String ALGORITHM =
            "AES/GCM/NoPadding";

    private static final int TAG_LENGTH =
            128;

    private static final int IV_LENGTH =
            12;
    private static final int SALT_LENGTH = 16;

    private CryptoManager() {
        // Utility class
    }


    /** Exposes the cipher transformation name for read-only display (e.g. in Settings). */
    public static String algorithm() {
        return ALGORITHM;
    }


    /** Exposes the GCM authentication tag length in bits for read-only display. */
    public static int tagLengthBits() {
        return TAG_LENGTH;
    }


    public static byte[] encrypt(
            byte[] data,
            SecretKey key
    ) {

        try {

            byte[] iv =
                    new byte[IV_LENGTH];


            SecureRandom random =
                    new SecureRandom();

            random.nextBytes(iv);


            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);


            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );


            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key,
                    spec
            );


            byte[] encrypted =
                    cipher.doFinal(data);


            byte[] result =
                    new byte[IV_LENGTH + encrypted.length];


            System.arraycopy(
                    iv,
                    0,
                    result,
                    0,
                    IV_LENGTH
            );


            System.arraycopy(
                    encrypted,
                    0,
                    result,
                    IV_LENGTH,
                    encrypted.length
            );


            return result;


        } catch (Exception e) {

            throw new IllegalStateException(
                    "Encryption failed.",
                    e
            );
        }
    }



    public static byte[] decrypt(
            byte[] encryptedData,
            SecretKey key
    ) {

        try {

            byte[] iv =
                    new byte[IV_LENGTH];


            System.arraycopy(
                    encryptedData,
                    0,
                    iv,
                    0,
                    IV_LENGTH
            );


            byte[] cipherText =
                    new byte[
                            encryptedData.length - IV_LENGTH
                            ];


            System.arraycopy(
                    encryptedData,
                    IV_LENGTH,
                    cipherText,
                    0,
                    cipherText.length
            );


            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);


            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    );


            cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    spec
            );


            return cipher.doFinal(cipherText);


        } catch (Exception e) {

            throw new IllegalStateException(
                    "Decryption failed. Wrong password or corrupted data.",
                    e
            );
        }
    }
}