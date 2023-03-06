package bank.customer.service;

import bank.model.Customer;

import java.util.List;

public interface ClientService {
    List<Customer> getAllClients();
    Customer getClientCpf(String cpf);
    Long getClientId(String cpf);

}

