package BankApplication.client.ServiceTests;

import BankApplication.account.controller.client.exceptions.CpfAlreadyExistsException;
import BankApplication.model.Client;
import BankApplication.account.controller.client.repository.ClientRepository;
import BankApplication.account.controller.client.request.ClientRequest;
import BankApplication.account.controller.client.service.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

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
        Mockito.when(clientRepository.existsByCpf(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(clientRepository.save(ArgumentMatchers.any(Client.class))).thenReturn(clientSaved.clientObjectRequest());

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