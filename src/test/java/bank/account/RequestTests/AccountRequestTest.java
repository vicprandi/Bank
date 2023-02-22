package bank.account.RequestTests;

import bank.account.request.AccountRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class AccountRequestTest {

    @Test
    public void AccountRequestTest() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(BigDecimal.valueOf(100.00));

        Assertions.assertEquals(BigDecimal.valueOf(100.00), accountRequest.getBalanceMoney());
    }
    @Test
    public void AccountRequest() {
        AccountRequest accountRequest = new AccountRequest();
        AccountRequest requested = accountRequest.requestAccount();

        Assertions.assertNotNull(requested);
    }


}
