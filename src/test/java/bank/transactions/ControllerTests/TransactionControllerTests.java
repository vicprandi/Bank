package bank.transactions.ControllerTests;


import bank.model.Transaction;
import bank.security.exceptions.CustomAuthorizationException;
import bank.transaction.request.TransactionRequest;
import bank.transaction.service.TransactionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnStatus200_afterGetAllTransactions() throws Exception {
        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_admin"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterGetAllTransactions_withUserScope() throws Exception {
        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        when(authentication.getAuthorities()).thenThrow(CustomAuthorizationException.class);
        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnStatus200_afterFindTransactionByClientId_withAdminScope() throws Exception {
        // Mocking transactionService
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        doReturn(transactions).when(transactionService).findTransactionByCustomerId(anyLong());

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/customer/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterFindTransactionByClientId_withoutAdminScope() throws Exception {
        // Simulate authentication without SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        when(authentication.getAuthorities()).thenThrow(CustomAuthorizationException.class);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/customer/{id}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomAuthorizationException));
    }

    @Test
    public void shouldReturnStatus200_afterDepositMoney_withUserScope() throws Exception {
        // Mocking transactionService to return a transaction
        Transaction transaction = new Transaction();
        when(transactionService.depositMoney(any(TransactionRequest.class))).thenReturn(transaction);

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_user"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Prepare the request body
        TransactionRequest request = new TransactionRequest();
        request.setValue(BigDecimal.valueOf(100));

        // Create an ObjectMapper to convert the TransactionRequest object to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldReturnStatus403_afterDepositMoney_withoutUserScope() throws Exception {
        // Simulate authentication without SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_admin"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Prepare the request body
        TransactionRequest request = new TransactionRequest();
        request.setValue(BigDecimal.valueOf(100));

        // Create an ObjectMapper to convert the TransactionRequest object to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus200_afterWithdrawMoney_withUserScope() throws Exception {
        // Mocking transactionService to return a transaction
        Transaction transaction = new Transaction();
        when(transactionService.withdrawMoney(any(TransactionRequest.class))).thenReturn(transaction);

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Prepare the request body
        TransactionRequest request = new TransactionRequest();
        request.setValue(BigDecimal.valueOf(100));

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw/{accountNumber}", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterWithdrawMoney_withoutUserScope() throws Exception {
        // Simulate authentication without SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Prepare the request body
        TransactionRequest request = new TransactionRequest();
        request.setValue(BigDecimal.valueOf(100));

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw/{accountNumber}", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertFalse(result.getResolvedException() instanceof CustomAuthorizationException));
    }

    @Test
    public void shouldReturnStatus200_afterTransferMoney_withUserScope() throws Exception {
        // Mocking transactionService to return a transaction ID
        Long transactionId = 123L;
        when(transactionService.transfer(any(BigDecimal.class), any(Long.class), any(Long.class))).thenReturn(transactionId);

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", "123456")
                        .param("destinationAccountNumber", "789012")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterTransferMoney_withoutUserScope() throws Exception {
        // Simulate authentication without SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenThrow(CustomAuthorizationException.class);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction/transfer")
                        .param("amount", "100")
                        .param("originAccountNumber", "123456")
                        .param("destinationAccountNumber", "789012")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomAuthorizationException));
    }
    @Test
    public void shouldReturnStatus200_afterGetTransaction_withAdminScope() throws Exception {
        // Mocking transactionService to return a transaction
        Transaction transaction = new Transaction();
        when(transactionService.findById(any(Long.class))).thenReturn(Optional.of(transaction));

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus200_afterGetTransaction_withUserScope() throws Exception {
        // Mocking transactionService to return a transaction
        Transaction transaction = new Transaction();
        when(transactionService.findById(any(Long.class))).thenReturn(Optional.of(transaction));

        // Simulate authentication with SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_user"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus403_afterGetTransaction_withoutAdminOrUserScope() throws Exception {
        // Simulate authentication without SCOPE_admin or SCOPE_user authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("SCOPE_other"));
        Mockito.when(authentication.getAuthorities()).thenThrow(CustomAuthorizationException.class);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomAuthorizationException));
    }
    @Test
    public void shouldReturnStatus404_afterGetTransaction_withNonExistingId() throws Exception {
        // Mocking transactionService to return an empty optional
        when(transactionService.findById(any(Long.class))).thenReturn(Optional.empty());

        // Simulate authentication with SCOPE_admin authority
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("SCOPE_admin"));
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/{transactionId}", 123)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}




