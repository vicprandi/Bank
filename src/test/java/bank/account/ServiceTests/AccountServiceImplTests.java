package bank.account.ServiceTests;

import bank.account.repository.AccountRepository;
import bank.account.service.AccountService;
import bank.account.service.AccountServiceImpl;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.model.Account;
import bank.model.Client;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");
        ClientRequest clientRequest2 = new ClientRequest("Thais", "12345678902", "02036020", "SE", "SP", "SP");

        Client client = new Client();
        client.setName("Victoria");
        client.setCpf("12345678901");
        client.setCity("SP");
        client.setState("SP");
        client.setStreet("SE");
        client.setPostalCode("02036020");
        clientRepository.save(client);

        Client client2 = new Client();
        client.setName("Thais");
        client.setCpf("12345678902");
        client.setCity("SP");
        client.setState("SP");
        client.setStreet("SE");
        client.setPostalCode("02036020");
        clientRepository.save(client2);
    }

    @Test
    public void shouldRegisterAccountByCpf() {
        // Cria um cliente de teste
        Client client = new Client();
        client.setCpf("12345678901");
        clientRepository.save(client);

        // Simula a busca pelo cliente recém-criado
        when(clientRepository.findByCpf("12345678901")).thenReturn(Optional.of(client));

        // Registra uma conta para o cliente
        Account account = accountServiceImpl.registerAccount("12345678901");

        // Verifica se a conta foi registrada corretamente
        assertNotNull(account);
        assertEquals("12345678901", account.getClient().getCpf());
    }
}
