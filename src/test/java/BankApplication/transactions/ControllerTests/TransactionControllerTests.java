package BankApplication.transactions.ControllerTests;

import BankApplication.account.repository.AccountRepository;
import BankApplication.account.request.AccountRequest;
import BankApplication.account.service.AccountServiceImpl;
import BankApplication.client.repository.ClientRepository;
import BankApplication.client.request.ClientRequest;
import BankApplication.client.service.ClientServiceImpl;
import BankApplication.model.Account;
import BankApplication.model.Transaction;
import BankApplication.transaction.controller.TransactionController;
import BankApplication.transaction.repository.TransactionRepository;
import BankApplication.transaction.service.TransactionServiceImpl;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {

    @MockBean
    private AccountServiceImpl accountService;

    @MockBean
    private ClientServiceImpl clientService;

    @MockBean
    private TransactionServiceImpl transactionService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void shouldReturnStatus201_afterGetAllTransactions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAllTransactions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactions"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterGetTransactionByClientId() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientRequest.clientObjectRequest().setId(1L);

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
        accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Long clientId = clientRequest.clientObjectRequest().getId();

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, account, new BigDecimal(300), Transaction.TransactionEnum.DEPOSIT),
                new Transaction(1L, account, new BigDecimal(200), Transaction.TransactionEnum.WITHDRAW)
        );

        when(transactionService.findTransactionByClientId(clientId)).thenReturn(transactions);

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/1", clientId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetTransactionByClientId() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientRequest.clientObjectRequest().setId(1L);

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
        accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Long clientId = clientRequest.clientObjectRequest().getId();

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, account, new BigDecimal(300), Transaction.TransactionEnum.DEPOSIT),
                new Transaction(1L, account, new BigDecimal(200), Transaction.TransactionEnum.WITHDRAW)
        );

        when(transactionService.findTransactionByClientId(clientId)).thenReturn(transactions);

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/1", clientId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterDepositMoney() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientRequest.clientObjectRequest().setId(1L);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(BigDecimal.valueOf(Long.parseLong("0")));

        Account account = new Account();
        account.setAccountNumber(accountRepository.generateAccountNumber());
        account.setClient(clientRequest.clientObjectRequest());
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        Account accountRegistered = accountService.registerAccount(accountRequest, clientRequest.getCpf());
        clientService.registerClient(clientRequest);
        accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Transaction transactionRequest = new Transaction();
        transactionRequest.setAccount(account);
        transactionRequest.setValue(BigDecimal.valueOf(100));
        transactionRequest.setTransactionType(Transaction.TransactionEnum.DEPOSIT);

        Long accountNumber = accountRepository.generateAccountNumber();
        account.setAccountNumber(accountNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("amount", transactionRequest.getValue());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);
        when(transactionService.depositMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/deposit/{accountNumber}", accountNumber)
                        .param("amount", transactionRequest.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterDepositMoney() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientRequest.clientObjectRequest().setId(1L);

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
        accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Transaction transactionRequest = new Transaction();
        transactionRequest.setAccount(account);
        transactionRequest.setValue(BigDecimal.valueOf(100));
        transactionRequest.setTransactionType(Transaction.TransactionEnum.DEPOSIT);

        Long accountNumber = accountRepository.generateAccountNumber();
        account.setAccountNumber(accountNumber);
        when(transactionService.depositMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterWithdrawMoney() throws Exception {
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setName("Victoria");
        clientRequest.setCity("SP");
        clientRequest.setStreet("SeiLa");
        clientRequest.setCpf("12345678901");
        clientRequest.setPostalCode("02036020");
        clientRequest.setState("SP");
        clientRequest.clientObjectRequest().setId(1L);

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
        accountService.registerAccount(accountRequest, clientRequest.getCpf());

        Transaction transactionRequest = new Transaction();
        transactionRequest.setAccount(account);
        transactionRequest.setValue(BigDecimal.valueOf(100));
        transactionRequest.setTransactionType(Transaction.TransactionEnum.WITHDRAW);

        Long accountNumber = accountRepository.generateAccountNumber();
        account.setAccountNumber(accountNumber);
        when(transactionService.depositMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);
        when(transactionService.withdrawMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        transactionService.depositMoney(accountNumber, new BigDecimal(100));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



}
