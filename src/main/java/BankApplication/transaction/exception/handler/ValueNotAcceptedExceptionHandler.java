package BankApplication.transaction.exception.handler;


import BankApplication.transaction.exception.ValueNotAcceptedException;
import BankApplication.transaction.exception.details.ValueNotAcceptedExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValueNotAcceptedExceptionHandler {
    @ExceptionHandler(ValueNotAcceptedException.class)
    public ResponseEntity<?> handlerValueAlreadyExistsException (ValueNotAcceptedException valueNotAcceptedException) {
        ValueNotAcceptedExceptionDetails exceptionDetails = ValueNotAcceptedExceptionDetails.valueNotAcceptedExceptionDetailsBuilder.newBuilder()
                .details(valueNotAcceptedException.getMessage())
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }
}
