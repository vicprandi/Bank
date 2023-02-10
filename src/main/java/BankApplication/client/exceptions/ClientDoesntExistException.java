package BankApplication.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientDoesntExistException extends RuntimeException {
    public ClientDoesntExistException(String message) {
        super(message);
    }
}