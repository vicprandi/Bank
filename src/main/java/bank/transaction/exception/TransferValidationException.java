package bank.transaction.exception;

public class TransferValidationException extends RuntimeException {

    public TransferValidationException(String message) {
        super(message);
    }
}
