package stegovault.exception;

public class StegoVaultException extends RuntimeException {

    public StegoVaultException(String message) {
        super(message);
    }


    public StegoVaultException(String message, Throwable cause) {
        super(message, cause);
    }
}