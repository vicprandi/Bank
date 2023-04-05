package bank.customer.exceptions.handler;

import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.details.ClientDoesntExistExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientDoesntExistExceptionHandler {

    @ExceptionHandler(CustomerDoesntExistException.class)
    public ResponseEntity<?> handlerClientDoesntExistException (CustomerDoesntExistException customerDoesntExistException) {
        ClientDoesntExistExceptionDetails clientDoesntExistExceptionDetails = ClientDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .details(customerDoesntExistException.getMessage())
                .build();

        return new ResponseEntity<>(clientDoesntExistExceptionDetails, HttpStatus.NOT_FOUND);
    }
}
