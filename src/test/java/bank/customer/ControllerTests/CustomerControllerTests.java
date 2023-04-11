package bank.customer.ControllerTests;

import bank.customer.controller.CustomerController;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;
import bank.customer.service.CustomerServiceImpl;


import bank.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTests {
    @MockBean private CustomerServiceImpl customerService;

    @MockBean private CustomerRepository customerRepository;

    @Autowired MockMvc mockMvc;

    @Spy
    CustomerRequest customerRequest;
    CustomerRequest customerRequest2;
    Customer customer;
    String invalidCpf;
    String sameCpf;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        customerRequest = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        customerRequest2 = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        customer = new Customer();

        String invalidCpf = "12345678900";
        String sameCpf = "12345678901";
    }

    @Test
    public void shouldReturnStatus201_afterGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus404_afterGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customer"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterGetACustomer() throws Exception {
        mockMvc.perform(get("/customers/" + customer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetACustomer() throws Exception {
        mockMvc.perform(get("/customerfdss/" + customer.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterCreateCustomer() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateCustomer() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        mockMvc.perform(post("/customerss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateCustomer() throws Exception {
        customerService.registerCustomer(customerRequest);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerRequest.setState("BH");
        customerService.updateCustomer(customerRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/customers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterUpdateCustomer() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        customerService.updateCustomer(customerRequest2);
        mockMvc.perform(MockMvcRequestBuilders.put("/customers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteCustomer() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        customerService.deleteCustomer(customerRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterDeleteCustomer() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        customerService.deleteCustomer(customerRequest2.getCpf());

        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    //Teste das exceções.
    //Customere não existe caso eu mande um CPF inválido para ele.
    @Test
    public void shouldReturnStatus404_afterGettingCustomerThatDoesntExist() throws Exception {
        when(customerService.getCustomerCpf(invalidCpf)).thenReturn(null);
        // then
        try {
            customerService.getCustomerId(invalidCpf);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
                mockMvc.perform(get("/customers/" + invalidCpf))
               .andExpect(status().isNotFound());
        }
    }

    //Testa se o CPF já existe. Se existir, joga uma exceção de customere já registrado e o teste passa.
    @Test
    public void shouldReturnStatus409_afterRegisterCustomerWithExistingCpf() throws Exception {
        // given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(true);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        // then
        try {
            customerService.registerCustomer(customerRequest);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
            mockMvc.perform(MockMvcRequestBuilders.get("/customer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            assertEquals("Customer already registred", ex.getMessage());
            verify(status().isConflict());
        }
    }

    //* O teste mostra que se você der update num customere que não existe, ele não vai funcionar pois não existe pelo CPF.**/
    @Test
    public void shouldReturnStatus404_afterUpdateCustomerWithExistingCpf() throws Exception {
        //given
        when(customerRepository.existsByCpf(invalidCpf)).thenReturn(null);

        try {
            customerService.getCustomerCpf(invalidCpf);
            fail("CustomerDoesntExistException was expected");
        } catch (CustomerDoesntExistException ex) {
            Assertions.assertEquals("Customer não existe!", ex.getMessage());
            verify(status().isNotFound());
        }
    }
}
