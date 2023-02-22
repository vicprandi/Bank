package bank.account.ServiceTests;


import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.exceptions.AccountDoesntExistException;
import bank.account.exceptions.CpfDoesntExistException;
import bank.account.repository.AccountRepository;
import bank.account.request.AccountRequest;
import bank.account.service.AccountService;
import bank.account.service.AccountServiceImpl;
import bank.model.Account;
import bank.model.Client;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.service.TransactionServiceImpl;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(ExceptionHandler.class)
public class AccountServiceImplTests {
    /*Antes dos testes*/
    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Mock
    private AccountService accountService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRequest accountRequest;

    @Mock
    private TransactionServiceImpl transactionService;

    @Spy
    ClientRequest clientRequest;
    ClientRequest clientRequest2;
    Client client;
    Client client2;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() {

        clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");
        clientRequest2 = new ClientRequest("Thais", "12345678902", "02036020", "SE", "SP", "SP");

        client = new Client();
        client.setName("Victoria");
        client.setCpf("12345678901");
        client.setCity("SP");
        client.setState("SP");
        client.setStreet("SE");
        client.setPostalCode("02036020");
        clientRepository.save(client);

        client2 = new Client();
        client2.setName("Thais");
        client2.setCpf("12345678902");
        client2.setCity("SP");
        client2.setState("SP");
        client2.setStreet("SE");
        client2.setPostalCode("02036020");
        clientRepository.save(client2);
    }

    @Test
    public void shouldRegisterAccount() {

        Long accountNumber = accountRepository.generateAccountNumber();
        // Cria um cliente de teste
        Client client = new Client();
        client.setCpf("12345678901");
        clientRepository.save(client);

        AccountRequest accountRequest = new AccountRequest();
        // Simula a busca pelo cliente recém-criado
        when(clientRepository.findByCpf("12345678901")).thenReturn(Optional.of(client));

        // Registra uma conta para o cliente
        Account account = new Account();
        account.setClient(client);
        account.setAccountNumber(accountNumber);
        account.setBalanceMoney(accountRequest.getBalanceMoney());
        // Verifica se a conta foi registrada corretamente
        assertNotNull(account);
        assertEquals("12345678901", account.getClient().getCpf());
    }
    @Test
    public void shouldThrowAccountAlreadyExistsException_whenRegisterAccount() {
        // Given
        String cpf = "12345678901";
        Account account = new Account();
        account.setAccountNumber(1L);
        account.setClient(new Client());
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(new Client()));
        when(accountRepository.generateAccountNumber()).thenReturn(account.getAccountNumber());
        when(accountRepository.existsByAccountNumber(account.getAccountNumber())).thenReturn(true);

        try {
            // When
            accountServiceImpl.registerAccount(cpf);
            fail("Expected AccountAlreadyExistsException to be thrown");
        } catch (AccountAlreadyExistsException e) {
            // Then
            assertEquals("Conta já registrada", e.getMessage());
        }
    }

    @Test
    public void shouldThrowCpfDoesntExistException_whenRegisterAccount() {
        // Given
        String cpf = "12345678909";
        Account account = new Account();
        account.setAccountNumber(1L);
        account.setClient(new Client());
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(new Client()));
        when(accountRepository.generateAccountNumber()).thenReturn(account.getAccountNumber());
        when(accountRepository.existsByAccountNumber(account.getAccountNumber())).thenReturn(false);
        when(clientRepository.existsByCpf(cpf)).thenReturn(null);

        try {
            // When
            accountServiceImpl.registerAccount(cpf);
            fail("Expected AccountAlreadyExistsException to be thrown");
        } catch (CpfDoesntExistException e) {
            // Then
            assertEquals("Cpf não existe", e.getMessage());
        }
    }
    @Test
    public void deleteAccountTest() {
        Long accountId = 1L;
        when(accountRepository.existsById(accountId)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(accountId);

        accountServiceImpl.deleteAccount(accountId);

        verify(accountRepository, times(1)).existsById(accountId);
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    @Test
    public void shouldThrowAccountDoesntExistException_whenDeleteAccount() {
        Long id = 123L;
        doThrow(new AccountDoesntExistException("Account doesn't exists!")).when(accountRepository).deleteById(id);

        try {
            accountServiceImpl.deleteAccount(id);
            fail("Expected AccountDoesntExistException to be thrown");
        } catch (AccountDoesntExistException ex) {
            assertEquals("Account doesn't exists!", ex.getMessage());
        }
    }

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        accounts.add(new Account());
        accounts.add(new Account());
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountServiceImpl.getAllAccounts();

        assertEquals(accounts.size(), result.size());
        assertEquals(accounts.get(0), result.get(0));
        assertEquals(accounts.get(1), result.get(1));
    }

    @Test
    public void testGetAllAccounts_ThrowAccountDoesntExistException() {

        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        try {
            accountServiceImpl.getAllAccounts();
            fail("Expected AccountDoesntExistException was not thrown");
        } catch (AccountDoesntExistException e) {
            assertEquals("Não há contas.", e.getMessage());
        }
    }

    @Test
    public void testFindAccountNumberByClientId_ReturnsAccountNumber() {
        Long clientId = 1L;
        Long accountNumber = 123456L;

        // Cria um objeto Client com uma conta associada
        Client client = new Client();
        client.setId(clientId);
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        client.setAccount(account);

        // Define o comportamento do mock do repository
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Chama o método que deve retornar o número da conta
        Long result = accountServiceImpl.findAccountNumberByClientId(clientId);

        // Verifica se o número da conta retornado é o esperado
        assertEquals(accountNumber, result);
    }

    @Test
    public void testFindAccountNumberByClientId_ThrowAccountDoesntExistException() {
        Long clientId = 1L;

        Client client = new Client();
        client.setId(clientId);

        Account account = new Account();
        account.setAccountNumber(null);
        account.setClient(client);

        client.setAccount(account);

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        try {
            accountServiceImpl.findAccountNumberByClientId(clientId);
            fail("Expected AccountDoesntExistException was not thrown");
        } catch (AccountDoesntExistException e) {
            assertEquals("Conta não existe!", e.getMessage());
        }
    }
    @Test
    public void testGetAccountById_ReturnsAccount() {
        Account account = new Account();
        Client client = new Client();
        client.setId(1L);
        client.setAccount(account);
        account.setClient(client);

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Optional<Account> returnedAccount = accountServiceImpl.getAccountById(1L);

        assertEquals(Optional.of(account), returnedAccount);
    }
    @Test
    public void testGetAccountById_ThrowAccountDoesntExistException() {
        // given
        Long clientId = 1L;
        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(new Client()));

        try {
            // when
            accountServiceImpl.getAccountById(clientId);
            fail("Expected AccountDoesntExistException was not thrown");
        } catch (AccountDoesntExistException e) {
            // then
            assertEquals("Não há conta.", e.getMessage());
        }
    }
}
