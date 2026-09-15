package stegovault.core;

import javax.crypto.SecretKey;

public class SessionManager {

    private final SecretKey key;


    public SessionManager(SecretKey key) {

        this.key = key;
    }


    public SecretKey getKey() {

        return key;
    }
}