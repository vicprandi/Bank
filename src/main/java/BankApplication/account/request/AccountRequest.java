package BankApplication.account.request;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class AccountRequest {

    @DecimalMin(value = "0.00", message = "Balance must be non-negative")
    private BigDecimal balanceMoney;

    public AccountRequest(){
    }

    public BigDecimal getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(BigDecimal balanceMoney) {
        this.balanceMoney = balanceMoney;
    }


    public AccountRequest requestAccount() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(this.balanceMoney);
        return accountRequest;
    }

}
