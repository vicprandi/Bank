package bank.client.ServiceTests;


import bank.account.repository.AccountRepository;
import bank.client.exceptions.ClientDoesntExistException;
import bank.client.exceptions.CpfAlreadyExistsException;
import bank.client.service.ClientService;
import bank.model.Account;
import bank.model.Client;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.client.service.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(ExceptionHandler.class)
public class ClientServiceImplTests {
    /*Antes dos testes*/
    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

    @Mock
    private ClientService clientService;

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
        client2.setName("Thais");
        client2.setCpf("12345678902");
        client2.setCity("SP");
        client2.setState("SP");
        client2.setStreet("SE");
        client2.setPostalCode("02036020");
        clientRepository.save(client2);
    }

    @Test
    public void shouldRegisterClient() {
        // when
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        //then
        Client result = clientServiceImpl.registerClient(clientRequest);

        //verify
        clientRepository.save(result);
        verify(clientRepository).save(result);
    }

    @Test
    public void shouldThrowCpfAlreadyExistsException_WhenRegisterClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientServiceImpl.registerClient(clientRequest)).thenThrow(CpfAlreadyExistsException.class);

        Client result = clientServiceImpl.registerClient(clientRequest);
        clientRepository.save(result);
        verify(clientRepository).save(result);
    }

    @Test
    public void shouldThrowCpfAlreadyExistsException() {
        String cpf = "12345678901";
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setCpf(cpf);
        when(clientRepository.existsByCpf(cpf)).thenReturn(true);

        // when
        try {
            clientServiceImpl.registerClient(clientRequest);
            fail("Expected CpfAlreadyExistsException was not thrown");
        } catch (CpfAlreadyExistsException ex) {
            // then
            assertEquals("Client already registred", ex.getMessage());
        }
    }
    @Test
    public void shouldReturnAllClients() {

        //given
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client2);

        List<Client> clients = new ArrayList<>();
        clients.add(client);
        clients.add(client2);
        when(clientRepository.findAll()).thenReturn(clients);

        // when
        List<Client> result = clientServiceImpl.getAllClients();

        // then
        verify(clientRepository, times(1)).findAll();
        assertEquals(2, result.size());
        assertTrue(result.contains(client));
        assertTrue(result.contains(client2));
    }

    @Test
    public void shouldThrowClientDoesntExistException_whenReturnAllClients() {
        // given
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        try {
            clientServiceImpl.getAllClients();
            fail("Expected ClientDoesntExistException to be thrown");
        } catch (ClientDoesntExistException ex) {
            // then
            assertEquals("Não há clientes", ex.getMessage());
        }
    }

    @Test
    public void shouldDeleteClient() {
        // given
        String cpf = "12345678901";
        Client client = new Client();
        Account account = new Account();
        client.setAccount(account);
        client.setCpf(cpf);

        when(clientRepository.existsByCpf(cpf)).thenReturn(true);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(client));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(clientRepository).delete(client);

        // when
        clientServiceImpl.deleteClient(cpf);

        // then
        verify(clientRepository, times(1)).delete(client);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void shouldThrowException_WhenDeletingNonExistentClient() {
        // given
        String cpf = "12345678901";
        when(clientRepository.existsByCpf(cpf)).thenReturn(false);

        // when and then
        verify(clientRepository, never()).findByCpf(cpf);
        verify(clientRepository, never()).delete(any(Client.class));
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    public void shouldThrowException_WhenDeleting_WhenCpfIsEmpty() {
        // given
        String cpf = "";
        Client client = new Client();
        Account account = new Account();
        client.setAccount(account);
        client.setCpf(cpf);

        when(clientRepository.existsByCpf(cpf)).thenReturn(true);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(client));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(clientRepository).delete(client);

        try {
            clientServiceImpl.deleteClient(client.getCpf());
            fail("Expected ClientDoesntExistException to be thrown");
        } catch (ClientDoesntExistException ex) {
            // then
            assertEquals("Client does not exist", ex.getMessage());
        }
    }

    @Test
    public void shouldReturnClientByCpf() {
        String cpf = "12345678901";
        Client client = new Client();
        Account account = new Account();
        client.setAccount(account);
        client.setCpf(cpf);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(client));

        // when
        Client result = clientServiceImpl.getClientCpf(cpf);

        // then
        verify(clientRepository, times(1)).findByCpf(cpf);
        assertEquals(client, result);
    }
    @Test
    public void shouldThrowClientDoesntExistException_whenReturnClientByCpf() {
        String cpf = "12345678901";
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // when
        try {
            clientServiceImpl.getClientCpf(cpf);
            fail("ClientDoesntExistException should have been thrown");
        } catch (ClientDoesntExistException e) {
            // then
            assertEquals("Cliente não existe!", e.getMessage());
        }
    }

    @Test
    public void shouldReturnClientId_byCpf() {
        String cpf = "12345678901";
        Long id = 1L;
        Client client = new Client();
        Account account = new Account();
        client.setAccount(account);
        client.setCpf(cpf);
        client.setId(id);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(client));

        // when
        Long result = clientServiceImpl.getClientId(cpf);

        // then
        verify(clientRepository, times(1)).findByCpf(cpf);
        assertEquals(id, client.getId());
    }

    @Test
    public void shouldThrowClientDoesntExistException_whenGetId() {
        String cpf = "12345678901";
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // when
        try {
            clientServiceImpl.getClientId(cpf);
            fail("ClientDoesntExistException should have been thrown");
        } catch (ClientDoesntExistException e) {
            // then
            assertEquals("Cliente não existe!", e.getMessage());
        }
    }

    @Test
    public void shouldUpdateClient() {
        String cpf = "12345678901";
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");

        Client client = new Client();
        client.setName("Victoria");
        client.setCpf("12345678901");
        client.setCity("SP");
        client.setState("SP");
        client.setStreet("SE");
        client.setPostalCode("02036020");

        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        Client updatedClient = clientServiceImpl.updateClient(clientRequest);

        verify(clientRepository).findByCpf(cpf);
        verify(clientRepository).save(client);

        assertEquals(clientRequest.getName(), updatedClient.getName());
        assertEquals(clientRequest.getCity(), updatedClient.getCity());
        assertEquals(clientRequest.getStreet(), updatedClient.getStreet());
        assertEquals(clientRequest.getState(), updatedClient.getState());
        assertEquals(clientRequest.getPostalCode(), updatedClient.getPostalCode());
    }
}
