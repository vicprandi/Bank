package bank.client.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CpfDoesntExistException extends RuntimeException {

    public CpfDoesntExistException(String message) {
        super(message);
    }
}
