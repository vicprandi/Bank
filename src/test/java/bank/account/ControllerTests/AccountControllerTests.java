package bank.account.ControllerTests;

import bank.account.controller.AccountController;

import bank.account.service.AccountServiceImpl;
import bank.model.Account;

import bank.security.exceptions.CustomAuthorizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AccountController.class)
class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnStatus200_afterGetAllAccounts() throws Exception {
        // Mocking accountService
        List<Account> accounts = Arrays.asList(new Account(), new Account());
        when(accountService.getAllAccounts()).thenReturn(accounts);

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus4xx_afterGetAllAccounts() throws Exception {
        // Simulate authentication without the required SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus201_afterRegisterAccount() throws Exception {
        // Mocking accountService
        Account newAccount = new Account();
        when(accountService.registerAccount(anyString())).thenReturn(newAccount);

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/{cpf}", "12345678900")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatus401_afterGettingOneAccountThatDoesntExist() throws Exception {
        // Mocking accountService
        when(accountService.getAccountById(anyLong())).thenThrow(new CustomAuthorizationException("Acesso negado"));

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus4xx_afterRegisterAccount() throws Exception {
        // Mocking accountService
        when(accountService.registerAccount(anyString())).thenThrow(new CustomAuthorizationException("Acesso negado"));

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/{cpf}", "12345678900")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus202_afterDeleteAccount() throws Exception {
        // Mocking accountService
        doNothing().when(accountService).deleteAccount(anyLong());

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    public void shouldReturnStatus4xx_afterDeleteClient() throws Exception {
        // Mocking accountService
        doThrow(new CustomAuthorizationException("Acesso negado")).when(accountService).deleteAccount(anyLong());

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //*Teste de exceções!*//
    @Test
    public void shouldReturnStatus404_afterRegisterAccountWithExistingCpf() throws Exception {
        // Mocking accountService
        when(accountService.registerAccount(anyString())).thenThrow(new CustomAuthorizationException("Acesso negado"));

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/{cpf}", "12345678900")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnStatus404_ifIdIsNotRegistred() throws Exception {
        // Mocking accountService
        when(accountService.getAccountById(anyLong())).thenThrow(new CustomAuthorizationException("Acesso negado"));

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testGetAccount() throws Exception {
        // Mocking accountService
        Account account = new Account();
        when(accountService.getAccountById(anyLong())).thenReturn(Optional.of(account));

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

