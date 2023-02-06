package BankApplication.ControllerTests;

import BankApplication.controller.ClientController;
import BankApplication.requests.ClientRequest;
import BankApplication.service.ClientServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ClientController.class)
public class ClientControllerTests {
    @MockBean private ClientServiceImpl clientService;
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
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientService.registerClient(clientRequest);
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientRequest.setState("BH");
        clientService.updateClient(clientRequest);
        mockMvc.perform(put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterUpdateClient() throws Exception {
        ClientRequest clientRequest2 = new ClientRequest();
        String requestBody = new ObjectMapper().valueToTree(clientRequest2).toString();
        clientService.updateClient(clientRequest2);
        mockMvc.perform(put("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        String requestBody = new ObjectMapper().valueToTree(clientRequest).toString();
        clientService.registerClient(clientRequest);
        clientService.deleteClient(clientRequest.clientObjectRequest().getId());
        mockMvc.perform(delete("/clients/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus404_afterDeletelient() throws Exception {
        ClientRequest clientRequest2 = new ClientRequest();
        String requestBody = new ObjectMapper().valueToTree(clientRequest2).toString();
        clientService.deleteClient(clientRequest2.clientObjectRequest().getId());
        mockMvc.perform(delete("/clients/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }
}
