package BankApplication.ServiceTests;

import BankApplication.exception.CpfAlreadyExistsException;
import BankApplication.model.Client;
import BankApplication.repository.ClientRepository;
import BankApplication.requests.ClientRequest;
import BankApplication.service.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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
        when(clientRepository.existsByCpf(anyString())).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenReturn(clientSaved.clientObjectRequest());

        //then
        Client clientRegistered = new Client();
        clientRegistered.setCpf("12345678901");

        clientService.registerClient(clientSaved);
        clientRepository.existsByCpf(clientSaved.getCpf());
        clientRepository.save(clientRegistered);

        assertEquals(clientSaved.getCpf(), clientRegistered.getCpf());
        verify(clientService).registerClient(clientSaved);

    }

}