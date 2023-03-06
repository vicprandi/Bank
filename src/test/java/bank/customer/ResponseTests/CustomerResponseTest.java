package bank.customer.ResponseTests;

import bank.customer.response.ClientResponse;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class CustomerResponseTest {

    @Test
    public void shouldCreateClientResponse() {
        String name = "Victoria";
        ClientResponse clientResponse = new ClientResponse(name);
        assertEquals(name, clientResponse.getName());
    }

}