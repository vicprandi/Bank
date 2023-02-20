package bank.client.repository;

import bank.model.Client;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Boolean existsByCpf(String cpf);

    @NotNull Optional<Client> findById(@NotNull Long id);

    Optional<Client> findByCpf(String cpf);
}
