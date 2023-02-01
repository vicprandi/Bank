package BankApplication.model;

import BankApplication.utils.TransactionEnum;
import jakarta.persistence.*;

@Entity
@Table(name= "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "account_id", foreignKey = @ForeignKey)
    @ManyToOne
    private Account account;

    @Column(name="value", nullable = false)
    private Long value;

    @Column(name="transaction_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType;

    public Transaction() {
    }

    public Transaction(Long id, Account account, Long value, TransactionEnum transactionType) {
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public TransactionEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionEnum transactionType) {
        this.transactionType = transactionType;
    }
}

