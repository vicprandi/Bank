package bank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "account")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", length = 50, nullable = false)
    private Long accountNumber;

    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey)
    @OneToOne
    @JsonIgnoreProperties({"id", "cpf", "createdData", "postalCode", "street"})
    private Customer customer;

    @OneToMany(targetEntity = Transaction.class, mappedBy = "originAccount")
    private List<Transaction> accountTransaction;

    @Column(name = "balance_money", nullable = false)
    private BigDecimal balanceMoney = BigDecimal.valueOf(0);

    @Column(name = "created_data")
    private LocalDate createdData = LocalDate.now();
}
