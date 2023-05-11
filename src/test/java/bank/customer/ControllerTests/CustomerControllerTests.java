package bank.customer.ControllerTests;

import bank.customer.controller.CustomerController;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;
import bank.customer.service.CustomerServiceImpl;


import bank.model.Customer;
import bank.security.exceptions.CustomAuthorizationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTests {
    @MockBean private CustomerServiceImpl customerService;

    @MockBean private CustomerRepository customerRepository;

    @Autowired MockMvc mockMvc;

    @Spy
    CustomerRequest customerRequest;
    CustomerRequest customerRequest2;

    Customer customer;
    String invalidCpf;
    String sameCpf;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        customerRequest = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");
        customerRequest2 = new CustomerRequest("Victoria", "12345678901", "02036020", "SE", "SP","SP");

        customer = new Customer();

        String invalidCpf = "12345678900";
        String sameCpf = "12345678901";
    }

    @Test
    public void shouldReturnStatus201_afterGetAllCustomers() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterGetAllCustomers() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        mockMvc.perform(get("/customer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnStatus404_afterGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))

                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterGetACustomer() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        mockMvc.perform(get("/customer/" + customer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterGetACustomer() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetACustomer() throws Exception {
        mockMvc.perform(get("/customerfdss/" + customer.getId()))

                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterCreateCustomer() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/customer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus4xx_afterCreateCustomer() throws Exception {
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        mockMvc.perform(post("/customerss")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterUpdateCustomer() throws Exception {

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        customerService.registerCustomer(customerRequest);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerRequest.setState("BH");
        customerService.updateCustomer(customerRequest);
        mockMvc.perform(MockMvcRequestBuilders.put("/customer/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus401_afterUpdateCustomer() throws Exception {
        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        customerService.registerCustomer(customerRequest);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerRequest.setState("BH");
        customerService.updateCustomer(customerRequest);
        // Perform the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/customer/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnStatus404_afterUpdateCustomer() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        customerService.updateCustomer(customerRequest2);
        mockMvc.perform(MockMvcRequestBuilders.put("/customers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteCustomer() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        customerService.deleteCustomer(customerRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus403_afterDeleteCustomer() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        customerService.registerCustomer(customerRequest);
        customerService.deleteCustomer(customerRequest.getCpf());
        mockMvc.perform(MockMvcRequestBuilders.delete("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void shouldReturnStatus404_afterDeleteCustomer() throws Exception {
        CustomerRequest customerRequest2 = new CustomerRequest();
        String requestBody = new ObjectMapper().valueToTree(customerRequest2).toString();
        customerService.deleteCustomer(customerRequest2.getCpf());

        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());
    }

    //Teste das exceções.
    //Customer não existe caso eu mande um CPF inválido para ele.
    @Test
    public void shouldReturnStatus404_afterGettingCustomerThatDoesntExist() throws Exception {
        when(customerService.getCustomerCpf(invalidCpf)).thenReturn(null);
        // then
        try {
            customerService.getCustomerId(invalidCpf);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
                mockMvc.perform(get("/customers/" + invalidCpf))
               .andExpect(status().isNotFound());
        }
    }

    //Testa se o CPF já existe. Se existir, joga uma exceção de customere já registrado e o teste passa.
    @Test
    public void shouldReturnStatus409_afterRegisterCustomerWithExistingCpf() throws Exception {
        // given
        when(customerRepository.existsByCpf(customerRequest.getCpf())).thenReturn(true);
        String requestBody = new ObjectMapper().valueToTree(customerRequest).toString();
        // then
        try {
            customerService.registerCustomer(customerRequest);
            fail("Deveria ter lançado a exceção CpfAlreadyExistsException");
        } catch (CpfAlreadyExistsException ex) {
            mockMvc.perform(MockMvcRequestBuilders.get("/customer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody));
            assertEquals("Customer already registred", ex.getMessage());
            verify(status().isConflict());
        }
    }

    //* O teste mostra que se você der update num customere que não existe, ele não vai funcionar pois não existe pelo CPF.**/
    @Test
    public void shouldReturnStatus404_afterUpdateCustomerWithExistingCpf() throws Exception {
        //given
        when(customerRepository.existsByCpf(invalidCpf)).thenReturn(null);

        try {
            customerService.getCustomerCpf(invalidCpf);
            fail("CustomerDoesntExistException was expected");
        } catch (CustomerDoesntExistException ex) {
            Assertions.assertEquals("Customer não existe!", ex.getMessage());
            verify(status().isNotFound());
        }
    }

    //**Lança a exceção da Authorization**/
    @Test
    public void shouldThrowCustomAuthorizationException_whenDeleteCustomerWithoutAdminScope() throws Exception {
        // Simulate authentication without SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/customer/{cpf}", "12345678900")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomAuthorizationException));
    }
}
