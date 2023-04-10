package bank.customer.ExceptionTests.DetailsTests;

import bank.customer.exceptions.details.ClientDoesntExistExceptionDetails;
import bank.customer.exceptions.details.CpfAlreadyExistsExceptionDetails;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DetailsTest {

    @Test
    public void testClientDoesntExistExceptionDetailsBuilder() {
        ClientDoesntExistExceptionDetails exceptionDetails = ClientDoesntExistExceptionDetails
                .ClientDoesntExistExceptionDetailsBuilder
                .newBuilder()
                .title("Title")
                .details("Details")
                .build();

        assertNotNull(exceptionDetails);
        assertEquals("Title", exceptionDetails.getTitle());
        assertEquals("Details", exceptionDetails.getDetails());
    }

    @Test
    public void testCpfAlreadyExistsExceptionDetailsBuilder() {
        CpfAlreadyExistsExceptionDetails exceptionDetails = CpfAlreadyExistsExceptionDetails
                .cpfAlreadyExistDetailsBuilder
                .newBuilder()
                .title("Title")
                .details("Details")
                .build();

        assertNotNull(exceptionDetails);
        assertEquals("Title", exceptionDetails.getTitle());
        assertEquals("Details", exceptionDetails.getDetails());
    }


}
