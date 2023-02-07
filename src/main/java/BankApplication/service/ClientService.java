package BankApplication.service;

import BankApplication.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> getAllClients();

    Client getClientCpf(String cpf);
    Optional<Client> getClient(Long id);
}
