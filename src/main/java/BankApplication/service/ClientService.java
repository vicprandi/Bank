package BankApplication.service;

import BankApplication.model.Client;

import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    Client findByCpf(String cpf);
}
