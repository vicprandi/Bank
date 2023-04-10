package bank.transaction.exception.handler;

import bank.transaction.exception.TransactionNotFoundException;
import bank.transaction.exception.details.TransactionNotFoundExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransactionNotFoundExceptionHandler {
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<?> handlerTransactionNotFoundException (TransactionNotFoundException transactionNotFoundException) {
        TransactionNotFoundExceptionDetails exceptionDetails =  TransactionNotFoundExceptionDetails.transactionNotFoundExceptionDetailsBuilder.newBuilder()
                .details(transactionNotFoundException.getMessage())
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }
}
