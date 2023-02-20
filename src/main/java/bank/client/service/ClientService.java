package bank.client.service;

import bank.model.Client;

import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    Client getClientCpf(String cpf);
    Long getClientId(String cpf);

}

