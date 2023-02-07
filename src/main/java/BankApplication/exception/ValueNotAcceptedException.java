package BankApplication.exception;

public class ValueNotAcceptedException extends RuntimeException {

    public ValueNotAcceptedException(String message) {
        super(message);
    }
}
