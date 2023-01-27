package BankApplication.handler;

import BankApplication.exception.CpfAlreadyExistsException;
import BankApplication.exception.CpfRegistredExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CpfAlreadyExistsExceptionHandler {

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<?> handlerCpfAlreadyExistsException (CpfAlreadyExistsException cpfAlreadyExistsException) {
        CpfRegistredExceptionDetails exceptionDetails = CpfRegistredExceptionDetails.cpfRegistredExceptionDetailsBuilder.newBuilder()
                .details(cpfAlreadyExistsException.getMessage())
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }

}
