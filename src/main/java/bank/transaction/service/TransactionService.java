package bank.transaction.service;

import bank.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    /* Achar conta pelo Id do CLiente */
    List<Transaction> findTransactionByCustomerId(Long id);

    Optional<Transaction> findById(Long transactionId);

    void executeTransfer(Long originTransactionId);

    Optional<Transaction> findByOriginAccount(Long accountNumber);

}
