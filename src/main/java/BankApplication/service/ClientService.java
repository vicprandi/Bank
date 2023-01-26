package BankApplication.service;

import BankApplication.model.Client;
import BankApplication.model.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /* Registrando o Cliente */
    public Client registerClient (ClientRequest clientRequest){

    }

    /*Alterando dados do Cliente*/

    /*Excluindo o Cliente*/







}

