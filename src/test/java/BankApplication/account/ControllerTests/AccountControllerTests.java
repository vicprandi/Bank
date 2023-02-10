package BankApplication.account.ControllerTests;


import BankApplication.account.controller.AccountController;
import BankApplication.account.repository.AccountRepository;
import BankApplication.account.request.AccountRequest;
import BankApplication.account.service.AccountServiceImpl;


import BankApplication.client.repository.ClientRepository;
import BankApplication.client.request.ClientRequest;
import BankApplication.client.service.ClientServiceImpl;
import BankApplication.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @MockBean private AccountServiceImpl accountService;

    @MockBean private ClientServiceImpl clientService;

    @MockBean private AccountRepository accountRepository;

    @MockBean private ClientRepository clientRepository;

    @Autowired MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    private AccountRequest accountRequest;

    @Test
    public void shouldReturnStatus201_afterGetAllAcounts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAllClients() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/account"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterRegisterAccount() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(BigDecimal.valueOf(Long.parseLong("0")));

        Account account = new Account();
        account.setAccountNumber(accountRepository.generateAccountNumber());
        account.setClient(clientRequest.clientObjectRequest());
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        Account accountRegistered = accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountRegistered", accountRegistered);
        responseMap.put("balanceMoney", accountRequest.getBalanceMoney());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);
        clientService.registerClient(clientRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/12345678901")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterRegisterAccount() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");


        /*Não criar o balance money*/
        Account account = new Account();
        account.setAccountNumber(accountRepository.generateAccountNumber());
        account.setClient(clientRequest.clientObjectRequest());
        Account accountRegistered = accountService.registerAccount(accountRequest, clientRequest.getCpf());

        String requestBody = new ObjectMapper().valueToTree(accountRegistered).toString();
        clientService.registerClient(clientRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/12345678901")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteAcount() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(BigDecimal.valueOf(Long.parseLong("0")));

        Account account = new Account();
        account.setAccountNumber(accountRepository.generateAccountNumber());
        account.setClient(clientRequest.clientObjectRequest());
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        Account accountRegistered = accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountRegistered", accountRegistered);
        responseMap.put("balanceMoney", accountRequest.getBalanceMoney());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);
        clientService.registerClient(clientRequest);
        accountService.deleteAccount(account.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }
    @Test
    public void shouldReturnStatus4xx_afterDeleteClient() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(BigDecimal.valueOf(Long.parseLong("0")));

        Account account = new Account();
        account.setAccountNumber(accountRepository.generateAccountNumber());
        account.setClient(clientRequest.clientObjectRequest());
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        Account accountRegistered = accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountRegistered", accountRegistered);
        responseMap.put("balanceMoney", accountRequest.getBalanceMoney());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);
        clientService.registerClient(clientRequest);
        accountService.deleteAccount(account.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
