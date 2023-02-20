package bank.account.request;

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
