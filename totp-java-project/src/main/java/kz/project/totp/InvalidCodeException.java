package kz.project.totp;

public class InvalidCodeException extends Exception {
    public InvalidCodeException(String message) {
        super(message);
    }
}
