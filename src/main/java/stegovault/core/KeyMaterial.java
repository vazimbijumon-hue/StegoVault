package stegovault.core;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Objects;

public final class KeyMaterial {

    private final SecretKey key;
    private final byte[] salt;

    public KeyMaterial(
            SecretKey key,
            byte[] salt
    ) {

        this.key = Objects.requireNonNull(key);
        this.salt = Objects.requireNonNull(salt).clone();
    }

    public SecretKey key() {
        return key;
    }

    public byte[] salt() {
        return Arrays.copyOf(salt, salt.length);
    }
}