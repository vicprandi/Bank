package bank.customer.ServiceTests;


import bank.account.repository.AccountRepository;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.service.CustomerService;
import bank.model.Account;
import bank.model.Customer;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;
import bank.customer.service.CustomerServiceImpl;
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
    private CustomerServiceImpl clientServiceImpl;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    CustomerRequest customerRequest;
    CustomerRequest customerRequest2;
    Customer customer;
    Customer customer2;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void setUp() {
        CustomerRequest customerRequest = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");
        CustomerRequest customerRequest2 = new CustomerRequest("Thais", "12345678902", "02036020", "SE", "SP", "SP");

        Customer customer = new Customer();
        customer.setName("Victoria");
        customer.setCpf("12345678901");
        customer.setCity("SP");
        customer.setState("SP");
        customer.setStreet("SE");
        customer.setPostalCode("02036020");
        customerRepository.save(customer);

        Customer customer2 = new Customer();
        customer2.setName("Thais");
        customer2.setCpf("12345678902");
        customer2.setCity("SP");
        customer2.setState("SP");
        customer2.setStreet("SE");
        customer2.setPostalCode("02036020");
        customerRepository.save(customer2);
    }

    @Test
    public void shouldRegisterCustomer() {
        // when
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        //then
        Customer result = clientServiceImpl.registerCustomer(customerRequest);

        //verify
        customerRepository.save(result);
        verify(customerRepository).save(result);
    }

    @Test
    public void shouldThrowCpfAlreadyExistsException() {
        String cpf = "12345678901";
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setCpf(cpf);
        when(customerRepository.existsByCpf(cpf)).thenReturn(true);

        // when
        try {
            clientServiceImpl.registerCustomer(customerRequest);
            fail("Expected CpfAlreadyExistsException was not thrown");
        } catch (CpfAlreadyExistsException ex) {
            // then
            assertEquals("Customer already registered", ex.getMessage());
        }
    }
    @Test
    public void shouldReturnAllCustomers() {

        //given
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer2);

        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        customers.add(customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // when
        List<Customer> result = clientServiceImpl.getAllCustomers();

        // then
        verify(customerRepository, times(1)).findAll();
        assertEquals(2, result.size());
        assertTrue(result.contains(customer));
        assertTrue(result.contains(customer2));
    }

    @Test
    public void shouldThrowCustomerDoesntExistException_whenReturnAllCustomers() {
        // given
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        try {
            clientServiceImpl.getAllCustomers();
            fail("Expected CustomerDoesntExistException to be thrown");
        } catch (CustomerDoesntExistException ex) {
            // then
            assertEquals("There's no customers", ex.getMessage());
        }
    }

    @Test
    public void shouldDeleteCustomer() {
        // given
        String cpf = "12345678901";
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);

        when(customerRepository.existsByCpf(cpf)).thenReturn(true);
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(customerRepository).delete(customer);

        // when
        clientServiceImpl.deleteCustomer(cpf);

        // then
        verify(customerRepository, times(1)).delete(customer);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void shouldThrowException_WhenDeletingNonExistentCustomer() {
        // given
        String cpf = "12345678901";
        when(customerRepository.existsByCpf(cpf)).thenReturn(false);

        // when and then
        verify(customerRepository, never()).findByCpf(cpf);
        verify(customerRepository, never()).delete(any(Customer.class));
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

        when(customerRepository.existsByCpf(cpf)).thenReturn(true);
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        doNothing().when(accountRepository).delete(account);
        doNothing().when(customerRepository).delete(customer);

        try {
            clientServiceImpl.deleteCustomer(customer.getCpf());
            fail("Expected CustomerDoesntExistException to be thrown");
        } catch (CustomerDoesntExistException ex) {
            // then
            assertEquals("Customer does not exist", ex.getMessage());
        }
    }

    @Test
    public void shouldReturnCustomerByCpf() {
        String cpf = "12345678901";
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));

        // when
        Customer result = clientServiceImpl.getCustomerCpf(cpf);

        // then
        verify(customerRepository, times(1)).findByCpf(cpf);
        assertEquals(customer, result);
    }
    @Test
    public void shouldThrowCustomerDoesntExistException_whenReturnCustomerByCpf() {
        String cpf = "12345678901";
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // when
        try {
            clientServiceImpl.getCustomerCpf(cpf);
            fail("CustomerDoesntExistException should have been thrown");
        } catch (CustomerDoesntExistException e) {
            // then
            assertEquals("There's no customers!", e.getMessage());
        }
    }

    @Test
    public void shouldReturnCustomerId_byCpf() {
        String cpf = "12345678901";
        Long id = 1L;
        Customer customer = new Customer();
        Account account = new Account();
        customer.setAccount(account);
        customer.setCpf(cpf);
        customer.setId(id);
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));

        // when
        Long result = clientServiceImpl.getCustomerId(cpf);

        // then
        verify(customerRepository, times(1)).findByCpf(cpf);
        assertEquals(id, customer.getId());
    }

    @Test
    public void shouldThrowCustomerDoesntExistException_whenGetId() {
        String cpf = "12345678901";
        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // when
        try {
            clientServiceImpl.getCustomerId(cpf);
            fail("CustomerDoesntExistException should have been thrown");
        } catch (CustomerDoesntExistException e) {
            // then
            assertEquals("There's no customers", e.getMessage());
        }
    }

    @Test
    public void shouldUpdateCustomer() {
        String cpf = "12345678901";
        CustomerRequest customerRequest = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP", "SP");

        Customer customer = new Customer();
        customer.setName("Victoria");
        customer.setCpf("12345678901");
        customer.setCity("SP");
        customer.setState("SP");
        customer.setStreet("SE");
        customer.setPostalCode("02036020");

        when(customerRepository.findByCpf(cpf)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer updatedCustomer = clientServiceImpl.updateCustomer(customerRequest);

        verify(customerRepository).findByCpf(cpf);
        verify(customerRepository).save(customer);

        assertEquals(customerRequest.getName(), updatedCustomer.getName());
        assertEquals(customerRequest.getCity(), updatedCustomer.getCity());
        assertEquals(customerRequest.getStreet(), updatedCustomer.getStreet());
        assertEquals(customerRequest.getState(), updatedCustomer.getState());
        assertEquals(customerRequest.getPostalCode(), updatedCustomer.getPostalCode());
    }
}
