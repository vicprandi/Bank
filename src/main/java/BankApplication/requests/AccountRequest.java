package BankApplication.requests;

import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.model.Transaction;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class AccountRequest {

    @NotNull(message = "{validation.field_required}")
    private Long accountNumber;

    @NotNull(message = "{validation.field_required}")
    private Long amount;

    @NotNull(message = "{validation.field_required}")
    private Long balanceMoney;

    @NotNull(message = "{validation.field_required}")
    private List<Transaction> accountTransaction;

    @NotNull(message = "{validation.field_required}")
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AccountRequest(){
    }

    public Long getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(Long balanceMoney) {
        this.balanceMoney = balanceMoney;
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
        account.setAccountTransaction(this.accountTransaction);
        account.setCreatedData(LocalDate.now());
        account.setBalanceMoney(this.balanceMoney);
        return account;
    }

}
