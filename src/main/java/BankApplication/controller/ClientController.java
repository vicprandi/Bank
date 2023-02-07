package BankApplication.controller;


import BankApplication.model.Client;
import BankApplication.requests.ClientRequest;

import BankApplication.service.ClientServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/clients")
@Api(value = "client")
public class ClientController {
    private final ClientServiceImpl clientService;

    @Autowired
    public ClientController(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    /*Para todos os CLientes*/
    @ApiOperation(value ="Bring all Clients")
    @GetMapping
    public List<Client> getAllClients() {
        logger.info("Retornando todos os clientes disponíveis");
        return clientService.getAllClients();
    }

    @ApiOperation(value = "Bring a specific client")
    @GetMapping("/{id}")
    public Optional<Client> getClient(@Valid Client client) {
        logger.info("Retornando cpf de cliente específico");
        return clientService.getClient(client.getId());
    }

    /* Registro do Cliente */
    @ApiOperation(value = "Client Register")
    @PostMapping
    public ResponseEntity<Client> registerClient (@RequestBody @Valid ClientRequest clientRequest) {
        Client client = clientService.registerClient(clientRequest);
        logger.info("Cliente Registrado");
        return new ResponseEntity<>(client, HttpStatus.CREATED);
    }

    /* Atualizar o Cliente */
    @ApiOperation(value = "Client Update")
    @PutMapping("/update")
    public ResponseEntity<Client> updateClient (@RequestBody @Valid ClientRequest cpf) {
        Client client = clientService.updateClient(cpf);
        logger.info("Cliente atualizado");
        return new ResponseEntity<Client>(client, HttpStatus.ACCEPTED);
    }

    /* Deletar o Cliente */
    @ApiOperation(value = "Deleting Client")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        logger.info("Cliente deletado");
        clientService.deleteClient(id);

        return new ResponseEntity<> (HttpStatus.ACCEPTED);
    }

}
