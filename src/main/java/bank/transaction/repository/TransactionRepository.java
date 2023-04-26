package bank.transaction.repository;

import bank.kafka.TransferStatus;
import bank.model.Account;
import bank.model.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsById(@NotNull Long id);

    @Query("SELECT t FROM Transaction t WHERE t.originAccount.accountNumber = :accountNumber AND t.transactionType = :transactionType")
    List<Transaction> findByAccountAndTransactionType(@Param("accountNumber") Long accountNumber, @Param("transactionType") Transaction.TransactionEnum transactionType);

    Optional<Transaction> findByOriginAccount(Long originAccount);

}