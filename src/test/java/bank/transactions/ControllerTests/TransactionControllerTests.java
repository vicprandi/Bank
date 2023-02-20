package bank.transactions.ControllerTests;

import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountServiceImpl;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.client.service.ClientServiceImpl;
import bank.model.Account;
import bank.model.Client;
import bank.model.Transaction;
import bank.transaction.controller.TransactionController;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.service.TransactionServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Spy
    ClientRequest clientRequest;
    ClientRequest clientRequest2;
    AccountRequest accountRequest;
    AccountRequest accountRequest2;

    Client client;

    Account account;

    BigDecimal balanceMoney;

    Long originAccountNumber;
    Long destinationAccountNumber;
    Long accountNumber;

    Account originAccount;
    Account destinationAccount;

    Transaction transactionRequest;
    Transaction transactionRequest2;

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

        accountService.registerAccount(clientRequest.getCpf());
        accountService.registerAccount(clientRequest2.getCpf());

        BigDecimal amount = BigDecimal.valueOf(100);

        Account originAccount = new Account();
        originAccount.setAccountNumber(accountRepository.generateAccountNumber());
        originAccount.setBalanceMoney(BigDecimal.valueOf(1000));

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber(accountRepository.generateAccountNumber());
        destinationAccount.setBalanceMoney(BigDecimal.valueOf(0));

        originAccountNumber = originAccount.getAccountNumber();
        destinationAccountNumber = destinationAccount.getAccountNumber();

        transactionRequest = new Transaction();
        transactionRequest.setAccount(account);
        transactionRequest.setValue(BigDecimal.valueOf(100));
        transactionRequest.setTransactionType(Transaction.TransactionEnum.DEPOSIT);

        transactionRequest2 = new Transaction();
        transactionRequest2.setAccount(account);
        transactionRequest2.setValue(BigDecimal.valueOf(100));
        transactionRequest2.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
    }

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

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountRegistered", accountRequest);
        responseMap.put("balanceMoney", accountRequest.getBalanceMoney());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);

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
        Long clientId = clientRequest.clientObjectRequest().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/transacFStions/1", clientId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterDepositMoney() throws Exception {
        when(transactionService.depositMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/deposit/{accountNumber}", accountNumber)
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterDepositMoney() throws Exception {
        when(transactionService.depositMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/deposit/{accountNumber}", accountNumber)
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterWithdrawMoney() throws Exception {
        when(transactionService.withdrawMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw/{accountNumber}", accountNumber)
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterWithdrawMoney() throws Exception {
        when(transactionService.withdrawMoney(accountNumber, new BigDecimal(100))).thenReturn(transactionRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/withdraw/{accountNumber}", accountNumber)
                        .param("amount", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterTransferMoney() throws Exception {
        when(accountRepository.findByAccountNumber(originAccountNumber)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(destinationAccount);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", originAccountNumber.toString())
                        .param("destinationAccountNumber", destinationAccountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterTransferMoney() throws Exception {
        when(accountRepository.findByAccountNumber(originAccountNumber)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(destinationAccount);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", originAccountNumber.toString())
                        .param("destinationAccountNumber", destinationAccountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
