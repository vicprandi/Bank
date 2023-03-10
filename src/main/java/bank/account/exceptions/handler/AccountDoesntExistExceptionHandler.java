package bank.account.exceptions.handler;

import bank.account.exceptions.AccountDoesntExistException;
import bank.account.exceptions.details.AccountDoesntExistExceptionDetails;
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
