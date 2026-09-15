package stegovault.exception;

public class InvalidPayloadException extends StegoVaultException {

    public InvalidPayloadException(String message) {
        super(message);
    }


    public InvalidPayloadException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}