package BankApplication.handler;

import BankApplication.exception.AccountDoesntExistException;
import BankApplication.exception.AccountDoesntExistExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountDoesntExistExceptionHandler {

    @ExceptionHandler(AccountDoesntExistException.class)
    public ResponseEntity<?> handlerAccountDoesntExistException (AccountDoesntExistException accountDoesntExistException) {
        AccountDoesntExistExceptionDetails accountDoesntExistExceptionDetails = AccountDoesntExistExceptionDetails.AccountDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .details(accountDoesntExistException.getMessage())
                .build();

        return new ResponseEntity<>(accountDoesntExistExceptionDetails, HttpStatus.NOT_FOUND);
    }
}
