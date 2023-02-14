package BankApplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="account_number", length = 50, nullable = false)
    @Pattern(regexp = "^\\d+$")
    private Long accountNumber;

    @JoinColumn(name = "client_id", foreignKey = @ForeignKey)
    @OneToOne
    private Client client;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "account")
    private List<Transaction> accountTransaction;

    @Column(name = "balance_money", nullable = false)
    @DecimalMin(value = "0.00", message = "Balance must be non-negative")
    private BigDecimal balanceMoney;

    @Column(name = "created_data")
    private LocalDate createdData = LocalDate.now();

    public Account(Long id, Long accountNumber, Client client, List<Transaction> accountTransaction, BigDecimal balanceMoney, LocalDate createdData) {
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

    public BigDecimal getBalanceMoney() {
        return balanceMoney;
    }

    public BigDecimal setBalanceMoney(BigDecimal balanceMoney) {
        this.balanceMoney = balanceMoney;
        return balanceMoney;
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

