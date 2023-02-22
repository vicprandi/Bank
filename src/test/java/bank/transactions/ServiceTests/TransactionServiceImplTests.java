package bank.transactions.ServiceTests;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountServiceImpl;
import bank.client.service.ClientServiceImpl;
import bank.model.Account;
import bank.model.Client;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.model.Transaction;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.service.TransactionService;
import bank.transaction.service.TransactionServiceImpl;
import org.junit.Before;
import org.junit.Test;

import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.math.BigDecimal;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Spy
    ClientRequest clientRequest;
    ClientRequest clientRequest2;
    AccountRequest accountRequest;
    AccountRequest accountRequest2;
    Transaction transaction;
    Client client;
    Client client2;
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
        transaction.setAccount(account);

        when(accountRepository.findByAccountNumber(eq(1L))).thenReturn(account);
        when(accountRepository.findByAccountNumber(eq(2L))).thenReturn(account2);
        when(transactionRepository.save(any())).thenReturn(transaction);
    }

    //*Depositar dinheiro*//
    @Test
    public void testDepositMoney() {
        Transaction transaction = transactionServiceImpl.depositMoney(1L, new BigDecimal("100.00"));
        transaction.setTransactionType(Transaction.TransactionEnum.DEPOSIT);
        assertEquals(new BigDecimal("100.00"), account.getBalanceMoney());

        assertEquals(Transaction.TransactionEnum.DEPOSIT, transaction.getTransactionType());
        assertEquals(new BigDecimal("100.00"), transaction.getValue());
        assertEquals(account, transaction.getAccount());
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testDepositMoneyWithNegativeAmount() {
        Long accountNumber = 1L;
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        transactionServiceImpl.depositMoney(accountNumber, negativeAmount);
    }

    //*Sacar dinheiro*//

    @Test
    public void testWithdrawMoney() {
        // Given
        BigDecimal initialBalance = new BigDecimal("500.00");
        BigDecimal withdrawalAmount = new BigDecimal("100.00");
        account.setAccountNumber(accountNumber);
        account.setBalanceMoney(initialBalance);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        // When
        Transaction transaction = transactionServiceImpl.withdrawMoney(accountNumber, withdrawalAmount);
        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);

        // Then
        assertEquals(new BigDecimal("400.00"), account.getBalanceMoney());

        assertEquals(Transaction.TransactionEnum.WITHDRAW, transaction.getTransactionType());
        assertEquals(withdrawalAmount, transaction.getValue());
        assertEquals(account, transaction.getAccount());
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testWithdrawMoneyWithNegativeAmount() {
        Long accountNumber = 1L;
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        transactionServiceImpl.withdrawMoney(accountNumber, negativeAmount);
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testWithdrawMoneyWithNegativeBalance() {
        BigDecimal initialBalance = new BigDecimal("-400.00");
        BigDecimal withdrawalAmount = new BigDecimal("100.00");
        account.setAccountNumber(accountNumber);
        account.setBalanceMoney(initialBalance);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        // When
        Transaction transaction = transactionServiceImpl.withdrawMoney(accountNumber, withdrawalAmount);
        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
        transactionServiceImpl.withdrawMoney(accountNumber, initialBalance);
    }
    @Test
    public void testTransferMoney() {
        BigDecimal transferAmount = new BigDecimal("50.00");

        // cria contas e adiciona saldo
        account.setBalanceMoney(new BigDecimal("100.00"));
        accountRepository.save(account);

        account2.setBalanceMoney(BigDecimal.ZERO);
        accountRepository.save(account2);

        // verifica se o método lançou uma exceção
        assertDoesNotThrow(() -> transactionServiceImpl.transferMoney(transferAmount, 1L, 2L));

        // verifica se a transferência foi realizada corretamente
        assertEquals(new BigDecimal("50.00"), account.getBalanceMoney());
        assertEquals(new BigDecimal("50.00"), account2.getBalanceMoney());
    }

    @Test
    public void transferMoney_shouldThrowAccountAlreadyExistsException() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(100.00);
        // Create account with balance

        account.setBalanceMoney(BigDecimal.valueOf(200.00));

        // Create destination account
        account.setAccountNumber(1L);
        account.setBalanceMoney(BigDecimal.valueOf(300.00));

        // Act and Assert
        assertThrows(AccountAlreadyExistsException.class, () -> {
            transactionServiceImpl.transferMoney(amount, accountNumber, accountNumber);
        });
    }

    @Test(expected = ValueNotAcceptedException.class)
    public void testTransferMoneyWithNegativeAmount() {
        BigDecimal transferAmount = new BigDecimal("-50.00");

        // cria contas e adiciona saldo
        account.setBalanceMoney(new BigDecimal("100.00"));
        accountRepository.save(account);

        account2.setBalanceMoney(BigDecimal.ZERO);
        accountRepository.save(account2);

        Transaction transaction = (Transaction) transactionServiceImpl.transferMoney(transferAmount, 1L, 2L);
        transaction.setTransactionType(Transaction.TransactionEnum.TRANSFER);
        transactionServiceImpl.transferMoney(transferAmount, 1L, 2L);
    }
}

