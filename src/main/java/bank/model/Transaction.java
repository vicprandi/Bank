package bank.model;

import bank.kafka.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name= "account_transactions")
@JsonInclude(JsonInclude.Include. NON_NULL)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "origin_account_id", foreignKey = @ForeignKey)
    @JsonIgnore
    @ManyToOne
    private Account originAccount;

    @JoinColumn(name="destination_account_id")
    @ManyToOne
    @JsonIgnore
    private Account destinationAccount;


    @Column(name="value", nullable = false)
    @DecimalMin(value = "0.00", message = "Value must be positive")
    private BigDecimal value;

    @Column(name="transaction_type", length = 50)
    @Enumerated(EnumType.STRING)
    private TransactionEnum transactionType;

    @Column(name="status", length = 50)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    public enum TransactionEnum {
        WITHDRAW,
        DEPOSIT,
        TRANSFER
    }
}