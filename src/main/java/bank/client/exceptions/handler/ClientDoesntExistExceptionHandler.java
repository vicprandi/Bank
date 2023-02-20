package bank.client.exceptions.handler;

import bank.client.exceptions.ClientDoesntExistException;
import bank.client.exceptions.details.ClientDoesntExistExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientDoesntExistExceptionHandler {

    @ExceptionHandler(ClientDoesntExistException.class)
    public ResponseEntity<?> handlerClientDoesntExistException (ClientDoesntExistException clientDoesntExistException) {
        ClientDoesntExistExceptionDetails clientDoesntExistExceptionDetails = ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .details(clientDoesntExistException.getMessage())
                .build();

        return new ResponseEntity<>(clientDoesntExistExceptionDetails, HttpStatus.NOT_FOUND);
    }
}
