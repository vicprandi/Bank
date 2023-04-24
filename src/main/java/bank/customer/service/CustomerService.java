package bank.customer.service;

import bank.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerCpf(String cpf);
    Long getCustomerId(String cpf);

}

