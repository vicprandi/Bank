package bank.account.ControllerTests;

import bank.account.controller.AccountController;
import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.exceptions.AccountDoesntExistException;
import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountServiceImpl;

import bank.client.request.ClientRequest;
import bank.client.service.ClientServiceImpl;
import bank.model.Account;
import bank.model.Client;


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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @MockBean private ClientServiceImpl clientService;

    @MockBean private AccountRepository accountRepository;

    @MockBean private AccountServiceImpl accountService;

    @Autowired MockMvc mockMvc;

    @Spy
    ClientRequest clientRequest;
    ClientRequest clientRequest2;
    AccountRequest accountRequest;
    AccountRequest accountRequest2;

    Client client;

    Account account;

    BigDecimal balanceMoney;

    Long accountNumber;

    @BeforeEach
    public void setUp() throws Exception {
        clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        clientRequest2 = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

        clientService.registerClient(clientRequest);
        clientService.registerClient(clientRequest2);

        accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(balanceMoney);

        accountRequest2 = new AccountRequest();
        accountRequest2.setBalanceMoney(balanceMoney);

        Account account = new Account();
        account.setId(1L);
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        accountNumber = accountRepository.generateAccountNumber();
        account.setAccountNumber(accountNumber);
        client = clientRequest.clientObjectRequest();
        account.setClient(client);

        Account account2 = new Account();

        account.setBalanceMoney(accountRequest2.getBalanceMoney());
        accountNumber = accountRepository.generateAccountNumber();
        account2.setAccountNumber(accountNumber);
        client = clientRequest2.clientObjectRequest();
        account2.setClient(client);

    }

    @Test
    public void shouldReturnStatus201_afterGetAllAcounts() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAllAcounts() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterRegisterAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/" + clientRequest.getCpf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void shouldReturnStatus201_afterGettingAccount() throws Exception {
        mockMvc.perform(get("/accounts/" + clientRequest.clientObjectRequest().getId())
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnStatus404_afterGettingOneAccountThatDoesntExist() throws Exception {
        mockMvc.perform(get("/accounts/" + null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus4xx_afterRegisterAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/accounsts/12345678901")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteAcount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }
    @Test
    public void shouldReturnStatus4xx_afterDeleteClient() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    //*Teste de exceções!*//
    @Test
    public void shouldReturnStatus404_afterRegisterAccountWithExistingCpf() throws Exception {
        // given
        when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(true);
        // then
        try {
            accountService.registerAccount(clientRequest.getCpf());
            fail("Deveria ter lançado a exceção AccountAlreadyExistsException");
        } catch (AccountAlreadyExistsException ex) {
            mockMvc.perform(get("/accounts/" + clientRequest.getCpf())
                    .contentType(MediaType.APPLICATION_JSON));
            assertEquals("Account already registred", ex.getMessage());
            verify(status().isNotFound());
        }
    }

    @Test
    public void shouldReturnStatus404_ifIdIsNotRegistred() throws Exception {
        // given
        when(accountRepository.existsById(client.getId())).thenReturn(false);
        // then
        try {
            accountService.registerAccount(clientRequest.getCpf());
            accountService.deleteAccount(client.getId());
            fail("Deveria ter lançado a exceção AccountDoesntExistException");
        } catch (AccountDoesntExistException ex) {
            mockMvc.perform(get("/accounts/delete/" + client.getId())
                    .contentType(MediaType.APPLICATION_JSON));
            assertEquals("Account doesn't exists!", ex.getMessage());
            verify(status().isNotFound());
        }
    }
    @Test
    public void testGetAccount() throws Exception {
        Long clientId = client.getId();
        mockMvc.perform(get("/accounts/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
