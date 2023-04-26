package bank.customer.ExceptionTests.HandlerTests;

import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.exceptions.details.CustomerDoesntExistExceptionDetails;
import bank.customer.exceptions.details.CpfAlreadyExistsExceptionDetails;
import bank.customer.exceptions.handler.CustomerDoesntExistExceptionHandler;
import bank.customer.exceptions.handler.CpfAlreadyExistsExceptionHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class HandlerTests {

    @Mock
    private CustomerDoesntExistException customerDoesntExistException;

    @InjectMocks
    private CustomerDoesntExistExceptionHandler handlerClientDoesntExistException;

    @Mock
    private CpfAlreadyExistsException cpfAlreadyExistsException;

    @InjectMocks
    private CpfAlreadyExistsExceptionHandler cpfAlreadyExistsExceptionHandler;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandlerClientDoesntExistException() {
        String errorMessage = "Customer does not exist";
        when(customerDoesntExistException.getMessage()).thenReturn(errorMessage);

        ResponseEntity<?> responseEntity = handlerClientDoesntExistException.handlerClientDoesntExistException(customerDoesntExistException);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);

        CustomerDoesntExistExceptionDetails exceptionDetails = (CustomerDoesntExistExceptionDetails) responseEntity.getBody();
        assertEquals(exceptionDetails.getDetails(), errorMessage);
        assertNull(exceptionDetails.getTitle());
    }
    @Test
    public void testHandlerCpfAlreadyExistsException() {
        String errorMessage = "Cpf already exists";
        when(cpfAlreadyExistsException.getMessage()).thenReturn(errorMessage);

        ResponseEntity<?> responseEntity = cpfAlreadyExistsExceptionHandler.handlerCpfAlreadyExistsException(cpfAlreadyExistsException);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);

        CpfAlreadyExistsExceptionDetails exceptionDetails = (CpfAlreadyExistsExceptionDetails) responseEntity.getBody();
        assertEquals(exceptionDetails.getDetails(), errorMessage);
        assertNull(exceptionDetails.getTitle());
    }

}
