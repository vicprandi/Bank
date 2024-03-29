package bank.transactions.ControllerTests;

import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountServiceImpl;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;
import bank.customer.service.CustomerServiceImpl;

import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import bank.transaction.controller.TransactionController;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.request.TransactionRequest;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import java.util.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {

    @MockBean
    private AccountServiceImpl accountService;

    @MockBean
    private CustomerServiceImpl customerService;

    @MockBean
    private TransactionServiceImpl transactionService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    CustomerRequest customerRequest;
    CustomerRequest customerRequest2;
    AccountRequest accountRequest;
    AccountRequest accountRequest2;

    Customer customer;

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
        customerRequest = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        customerRequest2 = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        customerService.registerCustomer(customerRequest);
        customerService.registerCustomer(customerRequest2);

        accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(balanceMoney);

        accountRequest2 = new AccountRequest();
        accountRequest2.setBalanceMoney(balanceMoney);

        Account account = new Account();

        account.setBalanceMoney(accountRequest.getBalanceMoney());
        accountNumber = accountRepository.generateAccountNumber();
        account.setAccountNumber(accountNumber);
        customer = customerRequest.customerObjectRequest();

        account.setCustomer(customer);

        Account account2 = new Account();

        account.setBalanceMoney(accountRequest2.getBalanceMoney());
        accountNumber = accountRepository.generateAccountNumber();
        account2.setAccountNumber(accountNumber);

        customer = customerRequest2.customerObjectRequest();

        account2.setCustomer(customer);

        accountService.registerAccount(customerRequest.getCpf());
        accountService.registerAccount(customerRequest2.getCpf());

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
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(BigDecimal.valueOf(100));
        transactionRequest.setTransactionType(Transaction.TransactionEnum.DEPOSIT);

        transactionRequest2 = new Transaction();
        transactionRequest2.setOriginAccount(account);
        transactionRequest2.setValue(BigDecimal.valueOf(100));
        transactionRequest2.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
    }

    @Test
    public void shouldReturnStatus201_afterGetAllTransactions() throws Exception {
        mockMvc.perform(get("/transaction"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAllTransactions() throws Exception {
        mockMvc.perform(get("/transactions"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterGetTransactionByCustomerId() throws Exception {

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accountRegistered", accountRequest);
        responseMap.put("balanceMoney", accountRequest.getBalanceMoney());

        String requestBody = new ObjectMapper().writeValueAsString(responseMap);

        Long customerId = customerRequest.customerObjectRequest().getId();
        mockMvc.perform(get("/transaction/1", customerId))

                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetTransactionByClientId() throws Exception {
        Long customerId = customerRequest.customerObjectRequest().getId();

        mockMvc.perform(get("/transacFStions/1", customerId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterDepositMoney() throws Exception {
        when(transactionService.depositMoney(any(TransactionRequest.class)))
                .thenReturn(transactionRequest);

        mockMvc.perform(post("/transaction/deposit/{accountNumber}", accountNumber)
                        .content(asJsonString(new TransactionRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterDepositMoney() throws Exception {
        when(transactionService.depositMoney(any(TransactionRequest.class)))
                .thenReturn(transactionRequest);

        mockMvc.perform(post("/transactions/deposit/{accountNumber}", accountNumber)
                        .content(asJsonString(new TransactionRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus200_afterWithdrawMoney() throws Exception {
        when(transactionService.withdrawMoney(any(TransactionRequest.class)))
                .thenReturn(transactionRequest);

        mockMvc.perform(post("/transaction/withdraw/{accountNumber}", accountNumber)
                        .content(asJsonString(new TransactionRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterWithdrawMoney() throws Exception {
        when(transactionService.withdrawMoney(any(TransactionRequest.class)))
                .thenReturn(transactionRequest);

        mockMvc.perform(post("/transactions/withdraw/{accountNumber}", accountNumber)
                        .content(asJsonString(new TransactionRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnStatus200_afterTransferMoney() throws Exception {
        when(accountRepository.findByAccountNumber(originAccountNumber)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(destinationAccount);

        mockMvc.perform(post("/transaction/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", originAccountNumber.toString())
                        .param("destinationAccountNumber", destinationAccountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterTransferMoney() throws Exception {
        when(accountRepository.findByAccountNumber(originAccountNumber)).thenReturn(null);
        when(accountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(null);

        mockMvc.perform(post("/transactions/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", originAccountNumber.toString())
                        .param("destinationAccountNumber", destinationAccountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testTransferMoney() throws Exception {
        // Arrange
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        Long originAccountNumber = 111111L;
        Long destinationAccountNumber = 222222L;
        Long transactionId = 123456L;

        when(transactionService.transfer(transferAmount, originAccountNumber, destinationAccountNumber)).thenReturn(transactionId);

        // Act & Assert
        mockMvc.perform(post("/transfer")
                        .param("amount", transferAmount.toString())
                        .param("originAccountNumber", originAccountNumber.toString())
                        .param("destinationAccountNumber", destinationAccountNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string(transactionId.toString()));
    }

    @Test
    void testGetTransaction_found() throws Exception {
        // Arrange
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        Long originAccountNumber = 111111L;
        Long destinationAccountNumber = 222222L;
        Long transactionId = 123456L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);

        when(transactionService.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act & Assert
        mockMvc.perform(get("/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().json(objectMapper.writeValueAsString(transaction)));
    }
    @Test
    void testGetTransaction_notFound() throws Exception {
        // Arrange
        Long transactionId = 123456L;

        when(transactionService.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect((ResultMatcher) content().string(""));
    }
}

