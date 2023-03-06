package bank.customer.repository;

import bank.model.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Customer, Long> {

    Boolean existsByCpf(String cpf);

    @NotNull Optional<Customer> findById(@NotNull Long id);

    Optional<Customer> findByCpf(String cpf);
}
