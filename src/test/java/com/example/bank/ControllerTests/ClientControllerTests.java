package com.example.bank.ControllerTests;

import BankApplication.controller.ClientController;
import BankApplication.model.Client;
import BankApplication.requests.ClientRequest;
import BankApplication.service.ClientServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = ClientController.class)
public class ClientControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ClientServiceImpl clientService;

    @Test
    public void shouldReturnStatus201_afterRegisterClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCpf("12345678910");
        clientRequest.setPostalCode("04637130");
        clientRequest.setStreet("XX");
        clientRequest.setStreet("SP");
        clientRequest.setCity("SP");

        mockMvc.perform(MockMvcRequestBuilders.post("/clients")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(clientRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Client searchClient = clientService.findByCpf(clientRequest.getCpf());
        Assertions.assertNotNull(searchClient);
    }
}
