package bank.client.ServiceTests;

import bank.client.exceptions.CpfAlreadyExistsException;
import bank.model.Client;
import bank.client.repository.ClientRepository;
import bank.client.request.ClientRequest;
import bank.client.service.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTests {

    /*Antes dos testes*/
    @Spy
    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldRegisterClient() throws CpfAlreadyExistsException {
        //given
        ClientRequest clientSaved = new ClientRequest();
        clientSaved.setCpf("12345678901");

        //when
        when(clientRepository.existsByCpf(ArgumentMatchers.anyString())).thenReturn(true);
        when(clientRepository.save(ArgumentMatchers.any(Client.class))).thenReturn(clientSaved.clientObjectRequest());

        //then
        Client clientRegistered = new Client();
        clientRegistered.setCpf("12345678901");

        clientService.registerClient(clientSaved);
        clientRepository.existsByCpf(clientSaved.getCpf());
        clientRepository.save(clientRegistered);

        Assertions.assertEquals(clientSaved.getCpf(), clientRegistered.getCpf());
        Mockito.verify(clientService).registerClient(clientSaved);

    }
}