package BankApplication.controller;

import BankApplication.model.Client;
import BankApplication.requests.ClientRequest;
import BankApplication.service.ClientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/clients")
@Api(value = "client")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    /* Registro do Cliente */
    @ApiOperation(value = "Client Register")
    @PostMapping
    public ResponseEntity<Client> registerClient (@RequestBody @Valid ClientRequest clientRequest) {
        Client client = clientService.registerClient(clientRequest);
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    /* Atualizar o Cliente */
    @ApiOperation(value = "Client Update")
    @PatchMapping()
    public ResponseEntity<Client> updateClient (@RequestBody @Valid ClientRequest clientRequest) {
        Client client = clientService.updateExistentClient(clientRequest);
        return new ResponseEntity<Client>(client, HttpStatus.ACCEPTED);
    }

    /* Deletar o Cliente */

    @ApiOperation(value = "Deleting Client")
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }

}
