package BankApplication.requests;

import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.model.Transaction;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

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
