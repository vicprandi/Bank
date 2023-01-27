package BankApplication.response;

import BankApplication.model.Client;

public class AccountResponse {

    private Long id;
    private Long accountNumber;
    private Client client;
    private Long amount;

    private Long balanceMoney;

    public AccountResponse(){
    }

    public AccountResponse(Long id, Long accountNumber, Client client, Long amount, Long balanceMoney) {
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

    public Long getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(Long balanceMoney) {
        this.balanceMoney = balanceMoney;
    }
}
