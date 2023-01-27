package BankApplication.repository;

import BankApplication.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Boolean existsByCPF(String CPF);
    Optional<Client> findByCPF(String CPF);

    Boolean existsByID(Long id);
    Optional<Client> findClientById (Long id);



}
