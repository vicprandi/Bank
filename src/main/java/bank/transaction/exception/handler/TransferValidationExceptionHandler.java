package bank.transaction.exception.handler;

import bank.transaction.exception.TransferValidationException;
import bank.transaction.exception.details.TransferValidationExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransferValidationExceptionHandler {
    @ExceptionHandler(TransferValidationException.class)
    public ResponseEntity<?> handlerTransferValidationException (TransferValidationException transferValidationException) {
        TransferValidationExceptionDetails exceptionDetails =  TransferValidationExceptionDetails.transferValidationExceptionDetailsBuilder.newBuilder()
                .details(transferValidationException.getMessage())
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }
}
