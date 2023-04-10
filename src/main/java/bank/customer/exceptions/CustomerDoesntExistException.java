package bank.customer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerDoesntExistException extends RuntimeException {
    public CustomerDoesntExistException(String message) {
        super(message);
    }
}
