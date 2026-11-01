package util;

public class InvalidStatusException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
