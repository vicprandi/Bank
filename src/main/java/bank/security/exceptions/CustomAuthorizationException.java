package bank.security.exceptions;

public class CustomAuthorizationException extends RuntimeException {
    public CustomAuthorizationException(String message) {
        super(message);
    }
}