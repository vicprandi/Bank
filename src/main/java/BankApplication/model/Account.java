package BankApplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="account_number", length = 50, nullable = false)
    private Long accountNumber;

    @JoinColumn(name = "client_id", foreignKey = @ForeignKey)
    @JsonIgnore
    @OneToOne
    private Client client;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "id")
    private List<Transaction> accountTransaction;

    @Column(name = "balance_money", nullable = false)
    private Long balanceMoney;

    @Column(name = "created_data")
    private LocalDate createdData = LocalDate.now();

    public Account(Long id, Long accountNumber, Client client, List<Transaction> accountTransaction, Long balanceMoney, LocalDate createdData) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.client = client;
        this.accountTransaction = accountTransaction;
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

    public List<Transaction> getAccountTransaction() {
        return accountTransaction;
    }

    public void setAccountTransaction(List<Transaction> accountTransaction) {
        this.accountTransaction = accountTransaction;
    }
}

