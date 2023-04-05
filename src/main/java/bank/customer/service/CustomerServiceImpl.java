package bank.customer.service;

import bank.customer.exceptions.CustomerDoesntExistException;
import bank.account.repository.AccountRepository;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.model.Account;
import bank.model.Customer;
import bank.customer.repository.CustomerRepository;
import bank.customer.request.CustomerRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    /* Serviço de registrar o Customer */
    public Customer registerCustomer(@Valid CustomerRequest customerRequest) {

        Customer customer = customerRequest.customerObjectRequest();

        if (customerRepository.existsByCpf(customerRequest.getCpf())) {
            throw new CpfAlreadyExistsException("Customer already registered");
        }

        return customerRepository.save(customer);
    }

    /* Serviço de atualizar os dados do customere */
    public Customer updateCustomer(@NotNull CustomerRequest customerRequest) {

        Customer customerObject = getCustomerCpf(customerRequest.getCpf());

        customerObject.setName(customerRequest.getName());
        customerObject.setCity(customerRequest.getCity());
        customerObject.setStreet(customerRequest.getStreet());
        customerObject.setState(customerRequest.getState());
        customerObject.setPostalCode(customerRequest.getPostalCode());

        return customerRepository.save(customerObject);
    }

    /* Serviço de deletar o customere*/
    public void deleteCustomer(String cpf) {
        Optional<Customer> customer = customerRepository.findByCpf(cpf);
        Account customerAccount = customer.get().getAccount();

        if (cpf.isEmpty()) throw new CustomerDoesntExistException("Customer does not exist");
        if (!customerRepository.existsByCpf(cpf)) throw new CustomerDoesntExistException("Customer does not exist");
        if (customerAccount != null) accountRepository.delete(customerAccount);

        customerRepository.delete(customer.get());
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if (!customers.isEmpty()) return customers;
        else throw new CustomerDoesntExistException("There's no customers");
    }

    @Override
    public Customer getCustomerCpf(String cpf) {

        Optional<Customer> customerObject = customerRepository.findByCpf(cpf);

        if (customerObject.isEmpty()) {
            throw new CustomerDoesntExistException("There's no customers!");
        }
        return customerObject.get();
    }

    public Long getCustomerId(String cpf) {

        Optional<Customer> customerObject = customerRepository.findByCpf(cpf);

        if (customerObject.isEmpty()) {
            throw new CustomerDoesntExistException("There's no customers");
        }
        return customerObject.get().getId();
    }
}

