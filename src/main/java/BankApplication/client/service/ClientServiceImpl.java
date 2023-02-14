package BankApplication.client.service;

import BankApplication.account.repository.AccountRepository;
import BankApplication.client.exceptions.ClientDoesntExistException;
import BankApplication.client.exceptions.CpfAlreadyExistsException;
import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.client.repository.ClientRepository;
import BankApplication.client.request.ClientRequest;
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
    public Client registerClient(@Valid ClientRequest clientRequest) {

        Client client = clientRequest.clientObjectRequest();

        if (clientRepository.existsByCpf(clientRequest.getCpf())) {
            throw new CpfAlreadyExistsException("Client already registred");
        }

        return clientRepository.save(client);
    }

    /* Serviço de atualizar os dados do cliente */
    public Client updateClient(@NotNull ClientRequest clientRequest) {

        Client clientObject = getClientCpf(clientRequest.getCpf());

        clientObject.setName(clientRequest.getName());
        clientObject.setCity(clientRequest.getCity());
        clientObject.setStreet(clientRequest.getStreet());
        clientObject.setState(clientRequest.getState());
        clientObject.setPostalCode(clientRequest.getPostalCode());

        return clientRepository.save(clientObject);
    }

    /* Serviço de deletar o cliente*/
    public void deleteClient(String cpf) {
        Optional<Client> client = clientRepository.findByCpf(cpf);
        Account clientAccount = client.get().getAccount();
        if (!clientRepository.existsByCpf(cpf)) throw new ClientDoesntExistException("Client does not exist");
        if (cpf.isEmpty()) throw new ClientDoesntExistException("Client does not exist");

        if (clientAccount != null) accountRepository.delete(clientAccount);

        clientRepository.delete(client.get());
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = clientRepository.findAll();

        if (!clients.isEmpty()) return clients;
        else throw new ClientDoesntExistException("Não há clientes");
    }

    @Override
    public Client getClientCpf(String cpf) {

        Optional<Client> clientObject = clientRepository.findByCpf(cpf);

        if (clientObject.isEmpty()) {
            throw new ClientDoesntExistException("Cliente não existe!");
        }
        return clientObject.get();
    }

    public Long getClientId(String cpf) {

        Optional<Client> clientObject = clientRepository.findByCpf(cpf);

        if (clientObject.isEmpty()) {
            throw new ClientDoesntExistException("Cliente não existe!");
        }
        return clientObject.get().getId();
    }


}

