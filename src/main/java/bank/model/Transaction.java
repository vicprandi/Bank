package bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
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

    public enum TransactionEnum {
        WITHDRAW,
        DEPOSIT,
        TRANSFER
    }
}