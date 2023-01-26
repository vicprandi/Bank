package BankApplication.requests;

import BankApplication.model.Account;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AccountRequest {

    @NotNull(message = "{validation.field_required}")
    private Long accountNumber;

    @NotNull(message = "{validation.field_required}")
    private Long amount;

    public AccountRequest(){
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Account requestAccount() {
        Account account = new Account();
        account.setAccountNumber(this.accountNumber);
        account.setAmount(this.amount);
        account.setClient(this.requestAccount().getClient());
        account.setCreatedData(LocalDate.now());
        return account;
    }

}
