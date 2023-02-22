package bank.client.ExceptionTests.HandlerTests;

import bank.client.exceptions.ClientDoesntExistException;
import bank.client.exceptions.CpfAlreadyExistsException;
import bank.client.exceptions.details.ClientDoesntExistExceptionDetails;
import bank.client.exceptions.details.CpfAlreadyExistsExceptionDetails;
import bank.client.exceptions.handler.ClientDoesntExistExceptionHandler;
import bank.client.exceptions.handler.CpfAlreadyExistsExceptionHandler;

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
    private ClientDoesntExistException clientDoesntExistException;

    @InjectMocks
    private ClientDoesntExistExceptionHandler handlerClientDoesntExistException;

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
        String errorMessage = "Client does not exist";
        when(clientDoesntExistException.getMessage()).thenReturn(errorMessage);

        ResponseEntity<?> responseEntity = handlerClientDoesntExistException.handlerClientDoesntExistException(clientDoesntExistException);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);

        ClientDoesntExistExceptionDetails exceptionDetails = (ClientDoesntExistExceptionDetails) responseEntity.getBody();
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
