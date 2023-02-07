package BankApplication.client.exceptions.handler;

import BankApplication.client.exceptions.CpfAlreadyExistsException;
import BankApplication.client.exceptions.details.CpfRegistredExceptionDetails;
import org.springframework.http.HttpStatus;
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
