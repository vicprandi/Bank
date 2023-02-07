package BankApplication.repository;

import BankApplication.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Random;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByAccountNumber(Long accountNumber);
    default Long generateAccountNumber() {
        Random rd = new Random();
        return rd.nextLong();
    };

}
