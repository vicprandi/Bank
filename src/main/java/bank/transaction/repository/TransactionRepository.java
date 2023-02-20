package bank.transaction.repository;

import bank.model.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsById(@NotNull Long id);

}
