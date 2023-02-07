package BankApplication.account.request;

import jakarta.validation.constraints.NotNull;

public class AccountRequest {

    @NotNull(message = "{validation.field_required}")
    private Long balanceMoney;

    public AccountRequest(){
    }

    public Long getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(Long balanceMoney) {
        this.balanceMoney = balanceMoney;
    }


    public AccountRequest requestAccount() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setBalanceMoney(this.balanceMoney);
        return accountRequest;
    }

}
