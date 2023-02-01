package BankApplication.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="account_number", length = 50, nullable = false)
    private Long accountNumber;

    @JoinColumn(name = "client_id", foreignKey = @ForeignKey)
    @OneToOne
    private Client client;

    @Column(name = "balance_money", nullable = false)
    private Long balanceMoney;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "created_data")
    private LocalDate createdData;

    public Account(Long id, Long accountNumber, Client client, Long balanceMoney, Long amount, LocalDate createdData) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.client = client;
        this.amount = amount;
        this.createdData = createdData;
        this.balanceMoney = balanceMoney;
    }

    public Account() {
    }

    public Long getId() {
        return id;
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

    public LocalDate getCreatedData() {
        return createdData;
    }

    public Long getBalanceMoney() {
        return balanceMoney;
    }

    public void setBalanceMoney(Long balanceMoney) {
        this.balanceMoney = balanceMoney;
    }

    public void setCreatedData(LocalDate createdData) {
        this.createdData = createdData;
    }

}

