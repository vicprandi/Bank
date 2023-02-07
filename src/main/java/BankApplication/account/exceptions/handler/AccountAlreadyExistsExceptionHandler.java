package BankApplication.account.exceptions.handler;

import BankApplication.account.exceptions.AccountAlreadyExistsException;
import BankApplication.account.exceptions.details.AccountAlreadyExistsExceptionDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccountAlreadyExistsExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<?> handlerAccountAlreadyExistsException (AccountAlreadyExistsException accountAlreadyExistsException) {
        AccountAlreadyExistsExceptionDetails accountAlreadyExistsExceptionDetails = AccountAlreadyExistsExceptionDetails.accountAlreadyExistsExceptionDetailsBuilder
                .newBuilder()
                .details(accountAlreadyExistsException.getMessage())
                .build();

        return new ResponseEntity<>(accountAlreadyExistsException, HttpStatus.NOT_FOUND);
    }
}
