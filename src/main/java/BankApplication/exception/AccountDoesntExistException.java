package BankApplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountDoesntExistException extends RuntimeException {
    public AccountDoesntExistException(String message) {
        super(message);
    }
}