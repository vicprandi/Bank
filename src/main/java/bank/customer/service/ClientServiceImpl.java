package bank.customer.service;

import bank.customer.exceptions.ClientDoesntExistException;
import bank.account.repository.AccountRepository;
import bank.customer.exceptions.CpfAlreadyExistsException;
import bank.model.Account;
import bank.model.Customer;
import bank.customer.repository.ClientRepository;
import bank.customer.request.ClientRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    /* Serviço de registrar o Cliente */
    public Customer registerClient(@Valid ClientRequest clientRequest) {

        Customer customer = clientRequest.clientObjectRequest();

        if (clientRepository.existsByCpf(clientRequest.getCpf())) {
            throw new CpfAlreadyExistsException("Customer already registered");
        }

        return clientRepository.save(customer);
    }

    /* Serviço de atualizar os dados do cliente */
    public Customer updateClient(@NotNull ClientRequest clientRequest) {

        Customer customerObject = getClientCpf(clientRequest.getCpf());

        customerObject.setName(clientRequest.getName());
        customerObject.setCity(clientRequest.getCity());
        customerObject.setStreet(clientRequest.getStreet());
        customerObject.setState(clientRequest.getState());
        customerObject.setPostalCode(clientRequest.getPostalCode());

        return clientRepository.save(customerObject);
    }

    /* Serviço de deletar o cliente*/
    public void deleteClient(String cpf) {
        Optional<Customer> client = clientRepository.findByCpf(cpf);
        Account clientAccount = client.get().getAccount();

        if (!clientRepository.existsByCpf(cpf)) throw new ClientDoesntExistException("Customer does not exist");
        if (cpf.isEmpty()) throw new ClientDoesntExistException("Customer does not exist");

        if (clientAccount != null) accountRepository.delete(clientAccount);

        clientRepository.delete(client.get());
    }

    @Override
    public List<Customer> getAllClients() {
        List<Customer> customers = clientRepository.findAll();

        if (!customers.isEmpty()) return customers;
        else throw new ClientDoesntExistException("There's no customers");
    }

    @Override
    public Customer getClientCpf(String cpf) {

        Optional<Customer> clientObject = clientRepository.findByCpf(cpf);

        if (clientObject.isEmpty()) {
            throw new ClientDoesntExistException("There's no clients!");
        }
        return clientObject.get();
    }

    public Long getClientId(String cpf) {

        Optional<Customer> clientObject = clientRepository.findByCpf(cpf);

        if (clientObject.isEmpty()) {
            throw new ClientDoesntExistException("There's no clients");
        }
        return clientObject.get().getId();
    }
}

