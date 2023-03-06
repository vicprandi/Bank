package bank.customer.ServiceTests;


import bank.account.repository.AccountRepository;
import bank.customer.exceptions.ClientDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.service.ClientService;
import bank.model.Account;
import bank.model.Customer;
import bank.customer.repository.ClientRepository;
import bank.customer.request.ClientRequest;
import bank.customer.service.ClientServiceImpl;
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
public class CustomerServiceImplTests {
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
    Customer customer;
    Customer customer2;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() {
        ClientRequest clientRequest = new ClientRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");
        ClientRequest clientRequest2 = new ClientRequest("Thais", "12345678902", "02036020", "SE", "SP", "SP");

        Customer customer = new Customer();
        customer.setName("Victoria");
        customer.setCpf("12345678901");
        customer.setCity("SP");
        customer.setState("SP");
        customer.setStreet("SE");
        customer.setPostalCode("02036020");
        clientRepository.save(customer);

        Customer customer2 = new Customer();
        customer2.setName("Thais");
        customer2.setCpf("12345678902");
        customer2.setCity("SP");
        customer2.setState("SP");
        customer2.setStreet("SE");
        customer2.setPostalCode("02036020");
        clientRepository.save(customer2);
    }

    @Test
    public void shouldRegisterClient() {
        // when
        when(clientRepository.save(any(Customer.class))).thenReturn(customer);

        //then
        Customer result = clientServiceImpl.registerClient(clientRequest);

        //verify
        clientRepository.save(result);
        verify(clientRepository).save(result);
    }

    @Test
    public void shouldThrowCpfAlreadyExistsException_WhenRegisterClient() {
        when(clientRepository.save(any(Customer.class))).thenReturn(customer);
        when(clientServiceImpl.registerClient(clientRequest)).thenThrow(CpfAlreadyExistsException.class);

        Customer result = clientServiceImpl.registerClient(clientRequest);
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
            assertEquals("Customer already registred", ex.getMessage());
        }
    }
    @Test
    public void shouldReturnAllClients() {

        //given
        when(clientRepository.save(any(Customer.class))).thenReturn(customer);
        when(clientRepository.save(any(Customer.class))).thenReturn(customer2);

        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        customers.add(customer2);
        when(clientRepository.findAll()).thenReturn(customers);

        // when
        List<Customer> result = clientServiceImpl.getAllClients();

        // then
        verify(clientRepository, times(1)).findAll();
        assertEquals(2, result.size());
        assertTrue(result.contains(customer));
        assertTrue(result.contains(customer2));
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
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);

        when(clientRepository.existsByCpf(cpf)).thenReturn(true);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(clientRepository).delete(customer);

        // when
        clientServiceImpl.deleteClient(cpf);

        // then
        verify(clientRepository, times(1)).delete(customer);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void shouldThrowException_WhenDeletingNonExistentClient() {
        // given
        String cpf = "12345678901";
        when(clientRepository.existsByCpf(cpf)).thenReturn(false);

        // when and then
        verify(clientRepository, never()).findByCpf(cpf);
        verify(clientRepository, never()).delete(any(Customer.class));
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    public void shouldThrowException_WhenDeleting_WhenCpfIsEmpty() {
        // given
        String cpf = "";
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);

        when(clientRepository.existsByCpf(cpf)).thenReturn(true);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(clientRepository).delete(customer);

        try {
            clientServiceImpl.deleteClient(customer.getCpf());
            fail("Expected ClientDoesntExistException to be thrown");
        } catch (ClientDoesntExistException ex) {
            // then
            assertEquals("Customer does not exist", ex.getMessage());
        }
    }

    @Test
    public void shouldReturnClientByCpf() {
        String cpf = "12345678901";
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));

        // when
        Customer result = clientServiceImpl.getClientCpf(cpf);

        // then
        verify(clientRepository, times(1)).findByCpf(cpf);
        assertEquals(customer, result);
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
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);
        customer.setId(id);
        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));

        // when
        Long result = clientServiceImpl.getClientId(cpf);

        // then
        verify(clientRepository, times(1)).findByCpf(cpf);
        assertEquals(id, customer.getId());
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

        Customer customer = new Customer();
        customer.setName("Victoria");
        customer.setCpf("12345678901");
        customer.setCity("SP");
        customer.setState("SP");
        customer.setStreet("SE");
        customer.setPostalCode("02036020");

        when(clientRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        when(clientRepository.save(customer)).thenReturn(customer);

        Customer updatedCustomer = clientServiceImpl.updateClient(clientRequest);

        verify(clientRepository).findByCpf(cpf);
        verify(clientRepository).save(customer);

        assertEquals(clientRequest.getName(), updatedCustomer.getName());
        assertEquals(clientRequest.getCity(), updatedCustomer.getCity());
        assertEquals(clientRequest.getStreet(), updatedCustomer.getStreet());
        assertEquals(clientRequest.getState(), updatedCustomer.getState());
        assertEquals(clientRequest.getPostalCode(), updatedCustomer.getPostalCode());
    }
}
