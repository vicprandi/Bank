package BankApplication.account.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class AccountRequest {

    private BigDecimal balanceMoney = BigDecimal.valueOf(0);

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
