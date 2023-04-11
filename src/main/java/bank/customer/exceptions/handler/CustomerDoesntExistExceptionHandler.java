package bank.customer.exceptions.handler;

import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.details.CustomerDoesntExistExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomerDoesntExistExceptionHandler {

    @ExceptionHandler(CustomerDoesntExistException.class)
    public ResponseEntity<?> handlerClientDoesntExistException (CustomerDoesntExistException customerDoesntExistException) {
        CustomerDoesntExistExceptionDetails customerDoesntExistExceptionDetails = CustomerDoesntExistExceptionDetails.ClientDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .details(customerDoesntExistException.getMessage())
                .build();

        return new ResponseEntity<>(customerDoesntExistExceptionDetails, HttpStatus.NOT_FOUND);
    }
}
