package bank.customer.ControllerTests;

import bank.customer.controller.ClientController;
import bank.customer.exceptions.ClientDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.repository.ClientRepository;
import bank.customer.request.ClientRequest;
import bank.customer.service.ClientServiceImpl;


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
@WebMvcTest(ClientController.class)
public class CustomerControllerTests {
    @MockBean private ClientServiceImpl clientService;

    @MockBean private ClientRepository clientRepository;

    @Autowired MockMvc mockMvc;

    @Spy
    ClientRequest clientRequest;
    ClientRequest clientRequest2;
    Customer customer;
    String invalidCpf;
    String sameCpf;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        clientRequest2 = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
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
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateClient() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clientss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateClient() throws Exception {
        clientService.registerClient(clientRequest);
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientRequest.setState("BH");
        clientService.updateClient(clientRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterUpdateClient() throws Exception {
        ClientRequest clientRequest2 = new ClientRequest();
        String requestBody = new ObjectMapper().valueToTree(clientRequest2).toString();
        clientService.updateClient(clientRequest2);
        mockMvc.perform(MockMvcRequestBuilders.put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteClient() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        clientService.deleteClient(clientRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterDeleteClient() throws Exception {
        ClientRequest clientRequest2 = new ClientRequest();
        String requestBody = new ObjectMapper().valueToTree(clientRequest2).toString();
        clientService.deleteClient(clientRequest2.getCpf());

        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    //Teste das exce????es.
    //Cliente n??o existe caso eu mande um CPF inv??lido para ele.
    @Test
    public void shouldReturnStatus404_afterGettingClientThatDoesntExist() throws Exception {
        when(clientService.getClientCpf(invalidCpf)).thenReturn(null);
        // then
        try {
            clientService.getClientId(invalidCpf);
            fail("Deveria ter lan??ado a exce????o CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
                mockMvc.perform(get("/clients/" + invalidCpf))
               .andExpect(status().isNotFound());
        }
    }

    //Testa se o CPF j?? existe. Se existir, joga uma exce????o de cliente j?? registrado e o teste passa.
    @Test
    public void shouldReturnStatus409_afterRegisterClientWithExistingCpf() throws Exception {
        // given
        when(clientRepository.existsByCpf(clientRequest.getCpf())).thenReturn(true);
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        // then
        try {
            clientService.registerClient(clientRequest);
            fail("Deveria ter lan??ado a exce????o CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
            mockMvc.perform(MockMvcRequestBuilders.get("/client")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            assertEquals("Customer already registred", ex.getMessage());
            verify(status().isConflict());
        }
    }

    //* O teste mostra que se voc?? der update num cliente que n??o existe, ele n??o vai funcionar pois n??o existe pelo CPF.**/
    @Test
    public void shouldReturnStatus404_afterUpdateClientWithExistingCpf() throws Exception {
        //given
        when(clientRepository.existsByCpf(invalidCpf)).thenReturn(null);

        try {
            clientService.getClientCpf(invalidCpf);
            fail("ClientDoesntExistException was expected");
        } catch (ClientDoesntExistException ex) {
            Assertions.assertEquals("Cliente n??o existe!", ex.getMessage());
            verify(status().isNotFound());
        }
    }
}
