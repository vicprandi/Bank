package bank.transaction.request;

import bank.model.Account;
import bank.model.Transaction;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequest {
    @NotNull(message = "{validation.field_required")
    private Account originAccount;

    @NotNull(message = "{validation.field_required")
    private BigDecimal value;

    public TransactionRequest(Account originAccount, BigDecimal value) {
        this.originAccount = originAccount;
        this.value = value;
    }

    public TransactionRequest() {
    }

    public Transaction transactionObjectRequest() {
        Transaction transaction = new Transaction();
        transaction.setValue(this.value);
        return transaction;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
