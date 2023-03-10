package bank.account.exceptions.handler;


import bank.account.exceptions.CpfDoesntExistException;
import bank.account.exceptions.details.CpfDoesntExistExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CpfDoesntExistExceptionHandler {

    @ExceptionHandler(CpfDoesntExistException.class)
    public ResponseEntity<?> handlerDoesntExistsException (CpfDoesntExistException cpfDoesntExistException) {
        CpfDoesntExistExceptionDetails exceptionDetails = CpfDoesntExistExceptionDetails.cpfDoesntExistDetailsBuilder.newBuilder()
                .details(cpfDoesntExistException.getMessage())
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.NOT_FOUND);
    }

}
