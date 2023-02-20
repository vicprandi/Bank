package BankApplication.client.ControllerTests;

import BankApplication.client.controller.ClientController;
import BankApplication.client.exceptions.ClientDoesntExistException;
import BankApplication.client.exceptions.CpfAlreadyExistsException;
import BankApplication.client.repository.ClientRepository;
import BankApplication.client.request.ClientRequest;
import BankApplication.client.service.ClientServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ClientController.class)
public class ClientControllerTests {
    @MockBean private ClientServiceImpl clientService;

    @MockBean private ClientRepository clientRepository;

    @Autowired MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    private ClientRequest clientRequest;

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
    public void shouldReturnStatus201_afterCreateClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clientss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

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
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        clientService.deleteClient(clientRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterDeletelient() throws Exception {
        ClientRequest clientRequest2 = new ClientRequest();
        String requestBody = new ObjectMapper().valueToTree(clientRequest2).toString();
        clientService.deleteClient(clientRequest2.getCpf());

        mockMvc.perform(MockMvcRequestBuilders.delete("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    //Teste das exceções.
    //Cliente não existe pelo CPF inválido.
    @Test
    public void getClientWithInvalidCpf() throws Exception {
        String invalidCpf = "12345678900";
        given(clientService.getClientCpf(invalidCpf)).willThrow(new ClientDoesntExistException("Cliente não existe!"));

        mockMvc.perform(get("/clients/" + invalidCpf))
                .andExpect(status().isNotFound());
    }

    //Testa se o CPF já existe.
    @Test
    public void testRegisterClientWithExistingCpf() throws Exception {
        //given
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        ClientRequest clientRequest2 = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        clientService.registerClient(clientRequest2);

        when(clientRepository.existsByCpf(clientRequest.getCpf())).thenReturn(true);
        given(clientService.registerClient(clientRequest)).willThrow(new CpfAlreadyExistsException("Client already registred"));

        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        //then
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                    .andExpect(status().isConflict());
    }
}
