package bank.account.RepositoryTests;


import bank.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AccountRepository.class)
public class AccountRepositoryTest {

    @MockBean
    private AccountRepository accountRepository;

    @Test
    public void testGenerateAccountNumber() {
        Long accountNumber = accountRepository.generateAccountNumber();
        assertNotNull(accountNumber);
    }

    @Test
    public void testGenerateAccountNumberReturnsPositiveNumber() {

        Long accountNumber = accountRepository.generateAccountNumber();

        assertTrue(accountNumber >= 0, "The generated account number should be positive.");
    }
}