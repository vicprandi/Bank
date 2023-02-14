package BankApplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Entity
@Table(name= "account_transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "account_id", foreignKey = @ForeignKey)
    @JsonIgnore
    @ManyToOne
    private Account account;

    @Column(name="value", nullable = false)
    @DecimalMin(value = "0.00", message = "Value must be positive")
    private BigDecimal value;

    @Column(name="transaction_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType;

    public Transaction() {
    }

    public Transaction(Long id, Account account, BigDecimal value, TransactionEnum transactionType) {
        this.id = id;
        this.account = account;
        this.value = value;
        this.transactionType = transactionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }


    public enum TransactionEnum {
        WITHDRAW,
        DEPOSIT,
        TRANSFER
    }


    public TransactionEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionEnum transactionType) {
        this.transactionType = transactionType;
    }

}