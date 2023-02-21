package bank.client.ResponseTests;

import bank.client.response.ClientResponse;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class ClientResponseTest {

    @Test
    public void shouldCreateClientResponse() {
        String name = "Victoria";
        ClientResponse clientResponse = new ClientResponse(name);
        assertEquals(name, clientResponse.getName());
    }

}