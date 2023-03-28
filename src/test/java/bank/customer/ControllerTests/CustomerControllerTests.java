package bank.customer.ControllerTests;

import bank.customer.controller.CustomerController;
import bank.customer.exceptions.ClientDoesntExistException;
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
    @MockBean private CustomerServiceImpl clientService;

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
        customerRequest = new CustomerRequest("Victoria",
                "12345678901",
                "02036020",
                "SE",
                "SP",
                "SP");
        customerRequest2 = new CustomerRequest("Victoria",
                "12345678901",
                "02036020",
                "SE",
                "SP",
                "SP");
        customer = new Customer();

        String invalidCpf = "12345678900";
        String sameCpf = "12345678901";
    }

    @Test
    public void shouldReturnStatus201_afterGetAllClients() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus404_afterGetAllClients() throws Exception {
        mockMvc.perform(get("/client"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterGetAClient() throws Exception {
        mockMvc.perform(get("/clients/" + customer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAClient() throws Exception {
        mockMvc.perform(get("/clientfdss/" + customer.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterCreateClient() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        clientService.registerCustomer(customerRequest);
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateClient() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        clientService.registerCustomer(customerRequest);
        mockMvc.perform(post("/clientss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateClient() throws Exception {
        clientService.registerCustomer(customerRequest);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerRequest.setState("BH");
        clientService.updateCustomer(customerRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterUpdateClient() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        clientService.updateCustomer(customerRequest2);
        mockMvc.perform(MockMvcRequestBuilders.put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteClient() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        clientService.registerCustomer(customerRequest);
        clientService.deleteCustomer(customerRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterDeleteClient() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        clientService.deleteCustomer(customerRequest2.getCpf());

        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    //Teste das exceções.
    //Cliente não existe caso eu mande um CPF inválido para ele.
    @Test
    public void shouldReturnStatus404_afterGettingClientThatDoesntExist() throws Exception {
        when(clientService.getCustomerCpf(invalidCpf)).thenReturn(null);
        // then
        try {
            clientService.getClientId(invalidCpf);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
                mockMvc.perform(get("/clients/" + invalidCpf))
               .andExpect(status().isNotFound());
        }
    }

    //Testa se o CPF já existe. Se existir, joga uma exceção de cliente já registrado e o teste passa.
    @Test
    public void shouldReturnStatus409_afterRegisterClientWithExistingCpf() throws Exception {
        // given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(true);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        // then
        try {
            clientService.registerCustomer(customerRequest);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
            mockMvc.perform(MockMvcRequestBuilders.get("/client")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            assertEquals("Customer already registred", ex.getMessage());
            verify(status().isConflict());
        }
    }

    //* O teste mostra que se você der update num cliente que não existe, ele não vai funcionar pois não existe pelo CPF.**/
    @Test
    public void shouldReturnStatus404_afterUpdateClientWithExistingCpf() throws Exception {
        //given
        when(customerRepository.existsByCpf(invalidCpf)).thenReturn(null);

        try {
            clientService.getCustomerCpf(invalidCpf);
            fail("ClientDoesntExistException was expected");
        } catch (ClientDoesntExistException ex) {
            Assertions.assertEquals("Cliente não existe!", ex.getMessage());
            verify(status().isNotFound());
        }
    }
}
