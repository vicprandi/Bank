package BankApplication.repository;

import BankApplication.model.Account;
import BankApplication.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByAccountNumber(Long accountNumber);

    Optional<Account> findByAccountNumber(Long accountNumber);
}
