package BankApplication.account.response;

import BankApplication.model.Client;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private Long accountNumber;
    private Client client;
    private Long amount;
    private BigDecimal balanceMoney = BigDecimal.valueOf(0);

    public AccountResponse(){
    }

    public AccountResponse(Long id, Long accountNumber, Client client, Long amount, BigDecimal balanceMoney) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.client = client;
        this.amount = amount;
        this.balanceMoney = balanceMoney;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(BigDecimal balanceMoney) {
        this.balanceMoney = balanceMoney;
    }
}
