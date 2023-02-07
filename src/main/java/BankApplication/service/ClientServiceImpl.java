package BankApplication.service;

import BankApplication.exception.ClientDoesntExistException;
import BankApplication.exception.CpfAlreadyExistsException;
import BankApplication.model.Client;
import BankApplication.repository.ClientRepository;
import BankApplication.requests.ClientRequest;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) throw new ClientDoesntExistException("Client does not exist");

        clientRepository.deleteById(id);
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = clientRepository.findAll();

        if (clients.isEmpty()) throw new ClientDoesntExistException("Não há clientes.");
        return clients;
    }

    public Optional<Client> getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);

        if (client.isEmpty()) throw new ClientDoesntExistException("Não há clientes.");
        return client;
    }

    @Override
    public Client getClientCpf(String cpf) {

        Optional<Client> clientObject = clientRepository.findByCpf(cpf);

        if (clientObject.isEmpty()) {
            throw new ClientDoesntExistException("Cliente não existe!");
        }
        return clientObject.get();
    }

}

