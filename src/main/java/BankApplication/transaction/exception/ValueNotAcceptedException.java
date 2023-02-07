package BankApplication.transaction.exception;

public class ValueNotAcceptedException extends RuntimeException {

    public ValueNotAcceptedException(String message) {
        super(message);
    }
}
