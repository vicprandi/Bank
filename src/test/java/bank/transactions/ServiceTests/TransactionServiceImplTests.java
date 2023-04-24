package bank.transactions.ServiceTests;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountServiceImpl;
import bank.customer.service.CustomerServiceImpl;
import bank.model.Account;
import bank.model.Customer;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;

import bank.model.Transaction;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.request.TransactionRequest;
import bank.transaction.service.TransactionService;
import bank.transaction.service.TransactionServiceImpl;
import org.junit.Before;
import org.junit.Test;

import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(ExceptionHandler.class)
public class TransactionServiceImplTests {

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl clientService;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;
    @Spy
    CustomerRequest customerRequest;
    CustomerRequest customerRequest2;
    AccountRequest accountRequest;
    AccountRequest accountRequest2;
    Transaction transaction;
    Customer customer;
    Customer customer2;
    Account account;
    Account account2;
    BigDecimal balanceMoney;
    Long accountNumber;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        account = new Account();
        account.setAccountNumber(1L);
        account.setBalanceMoney(BigDecimal.ZERO);

        account2 = new Account();
        account2.setAccountNumber(2L);
        account2.setBalanceMoney(BigDecimal.ZERO);

        transaction = new Transaction();
        transaction.setValue(new BigDecimal("100.00"));
        transaction.setOriginAccount(account);

        when(accountRepository.findByAccountNumber(eq(1L))).thenReturn(account);
        when(accountRepository.findByAccountNumber(eq(2L))).thenReturn(account2);
        when(transactionRepository.save(any())).thenReturn(transaction);
    }

    //*Depositar dinheiro*//
    @Test
    public void testDepositMoney() {
        TransactionRequest request = new TransactionRequest();
        request.setValue(new BigDecimal("100.00"));
        Account account = new Account();
        account.setId(1L);
        request.setOriginAccount(account);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction transaction = transactionServiceImpl.depositMoney(request);

        assertEquals(new BigDecimal("100.00"), transaction.getValue());
        assertEquals(account, transaction.getOriginAccount());
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testDepositMoneyWithNegativeAmount() {
        Account account = new Account();
        account.setAccountNumber(1L);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(new BigDecimal("-100.00"));
        transactionServiceImpl.depositMoney(transactionRequest);
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testDepositMoneyWithNegativeBalance() {
        // create a mock account
        Account account = new Account();
        account.setBalanceMoney(new BigDecimal("-100"));

        // create a mock TransactionRequest
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(new BigDecimal("50"));

        // mock the account repository to return the mock account
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(account);

        // try to deposit a positive amount, which should trigger a ValueNotAcceptedException
        transactionServiceImpl.depositMoney(transactionRequest);
    }

    //*Sacar dinheiro*//
    @Test
    public void testWithdrawMoney() {
        // Given
        BigDecimal initialBalance = new BigDecimal("500.00");
        BigDecimal withdrawalAmount = new BigDecimal("100.00");
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(withdrawalAmount);
        account.setAccountNumber(accountNumber);
        account.setBalanceMoney(initialBalance);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        // When
        Transaction transaction = transactionServiceImpl.withdrawMoney(transactionRequest);
        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);

        // Then
        assertEquals(new BigDecimal("400.00"), account.getBalanceMoney());

        assertEquals(Transaction.TransactionEnum.WITHDRAW, transaction.getTransactionType());
        assertEquals(withdrawalAmount, transaction.getValue());
        assertEquals(account, transaction.getOriginAccount());
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testWithdrawMoneyWithNegativeAmount() {
        Long accountNumber = 1L;
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(negativeAmount);
        transactionServiceImpl.withdrawMoney(transactionRequest);
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testWithdrawMoneyWithNegativeBalance() {
        BigDecimal initialBalance = new BigDecimal("-400.00");
        BigDecimal withdrawalAmount = new BigDecimal("100.00");
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOriginAccount(account);
        transactionRequest.setValue(withdrawalAmount);
        account.setAccountNumber(accountNumber);
        account.setBalanceMoney(initialBalance);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        // When
        Transaction transaction = transactionServiceImpl.withdrawMoney(transactionRequest);
        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
        transactionServiceImpl.withdrawMoney(transactionRequest);
    }

    @Test
    public void testTransferMoney() {
        BigDecimal transferAmount = new BigDecimal("50.00");

        // cria contas e adiciona saldo
        account.setBalanceMoney(new BigDecimal("100.00"));
        accountRepository.save(account);

        account2.setBalanceMoney(BigDecimal.ZERO);
        accountRepository.save(account2);

        // verifica se a transferência foi realizada corretamente
        assertEquals(new BigDecimal("100.00"), account.getBalanceMoney());
        assertEquals(new BigDecimal("100.00"), account2.getBalanceMoney());
    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void testTransferMoneyWithSameOriginAndDestinationAccounts() {
        BigDecimal transferAmount = new BigDecimal("50.00");

        // cria contas e adiciona saldo
        account.setBalanceMoney(new BigDecimal("100.00"));
        accountRepository.save(account);

        // verifica se o método lançou uma exceção
        transactionServiceImpl.transfer(transferAmount, account.getAccountNumber(), account.getAccountNumber());

        fail("Should have thrown AccountAlreadyExistsException");
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testTransferMoneyWithNegativeAmount() {
        BigDecimal transferAmount = new BigDecimal("-50.00");

        // cria contas e adiciona saldo
        account.setBalanceMoney(new BigDecimal("100.00"));
        accountRepository.save(account);

        account2.setBalanceMoney(BigDecimal.ZERO);
        accountRepository.save(account2);

        transactionServiceImpl.transfer(transferAmount, account.getAccountNumber(), account2.getAccountNumber());
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testTransferMoneyWithNegativeBalance() {
        BigDecimal amount = new BigDecimal("1000");
        Account originAccount = new Account();
        originAccount.setAccountNumber(1234L);
        originAccount.setBalanceMoney(new BigDecimal("-500")); // saldo negativo
        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber(5678L);
        destinationAccount.setBalanceMoney(BigDecimal.ZERO);

        when(accountRepository.findByAccountNumber(1234L)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumber(5678L)).thenReturn(destinationAccount);

        transactionServiceImpl.transfer(amount, 1234L, 5678L);
    }

    @Test(expected = RuntimeException.class)
    public void testTransferMoneyWithTransactionSaveError() {
        BigDecimal amount = new BigDecimal("100");
        Account originAccount = new Account();
        originAccount.setAccountNumber(1L);
        originAccount.setBalanceMoney(BigDecimal.valueOf(500));

        Account destinationAccount = new Account();
        destinationAccount.setAccountNumber(2L);
        destinationAccount.setBalanceMoney(BigDecimal.valueOf(1000));

        doThrow(new RuntimeException()).when(transactionRepository).save(any(Transaction.class));

        when(accountRepository.findByAccountNumber(1L)).thenReturn(originAccount);
        when(accountRepository.findByAccountNumber(2L)).thenReturn(destinationAccount);

        transactionServiceImpl.transfer(amount, 1L, 2L);
    }


    @Test(expected = RuntimeException.class)
    public void testGetAllTransactionsThrowsExceptionWhenEmpty() {
        when(transactionServiceImpl.getAllTransactions()).thenReturn(List.of());
        transactionServiceImpl.getAllTransactions();
    }

    @Test
    public void testGetAllTransactionsReturnsListWhenNotEmpty() {
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setOriginAccount(new Account());
        t1.setTransactionType(Transaction.TransactionEnum.DEPOSIT);
        t1.setValue(BigDecimal.valueOf(100.00));

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setOriginAccount(new Account());
        t2.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
        t2.setValue(BigDecimal.valueOf(50.00));

        Mockito.when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Transaction> transactions = transactionServiceImpl.getAllTransactions();

        assertTrue(transactions.contains(t1));
        assertTrue(transactions.contains(t2));
        assertEquals(2, transactions.size());
    }
}

