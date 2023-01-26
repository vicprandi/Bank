package BankApplication.model.repository;

import BankApplication.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Boolean existsByCPF(String cpf);

    Optional<Client> findByCPF(String cpf);

}
