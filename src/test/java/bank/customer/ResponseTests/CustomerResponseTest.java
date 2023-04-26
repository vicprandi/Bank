package bank.customer.ResponseTests;

import bank.customer.response.CustomerResponse;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class CustomerResponseTest {

    @Test
    public void shouldCreateClientResponse() {
        String name = "Victoria";
        CustomerResponse customerResponse = new CustomerResponse(name);
        assertEquals(name, customerResponse.getName());
    }

}