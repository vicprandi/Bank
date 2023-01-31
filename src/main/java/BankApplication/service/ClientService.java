package BankApplication.service;

import BankApplication.exception.ClientDoesntExistException;
import BankApplication.exception.CpfAlreadyExistsException;
import BankApplication.model.Client;
import BankApplication.repository.ClientRepository;
import BankApplication.requests.ClientRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /* Serviço de registrar o Cliente */
    public Client registerClient (@Valid ClientRequest clientRequest){

        Client client = clientRequest.clientObjectRequest();

        if (clientRepository.existsByCpf(clientRequest.getCpf())) {
            throw new CpfAlreadyExistsException("Client already registred");
        }

        return clientRepository.save(client);
    }

    /* Serviço de atualizar os dados do cliente */
    public Client updateExistentClient(ClientRequest clientRequest) {

        Optional<Client> clientObject = clientRepository.findByCpf(clientRequest.getCpf());

        if (clientObject.isEmpty()) throw new ClientDoesntExistException("Client does not exist");

        Client clientGet = clientObject.get();

        clientGet.setName(clientRequest.getName());
        clientGet.setCpf(clientRequest.getCpf());
        clientGet.setCity(clientRequest.getCity());
        clientGet.setStreet(clientRequest.getStreet());
        clientGet.setState(clientRequest.getState());
        clientGet.setPostalCode(clientRequest.getPostalCode());

        return clientRepository.save(clientGet);
    }

    /* Serviço de deletar o cliente*/
    public void deleteClient (Long id) {
        if (!clientRepository.existsById(id)) throw new ClientDoesntExistException("Client does not exist");

        clientRepository.deleteById(id);
    }
}

