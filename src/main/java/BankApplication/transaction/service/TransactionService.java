package BankApplication.transaction.service;

import BankApplication.model.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    /* Achar conta pelo Id do CLiente */
    List<Transaction> findTransactionByClientId(Long id);
}
