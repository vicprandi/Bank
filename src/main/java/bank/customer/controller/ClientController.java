package bank.customer.controller;


import bank.customer.service.ClientServiceImpl;
import bank.model.Customer;
import bank.customer.request.ClientRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.transaction.Transactional;
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
    private static final Logger logger = Logger.getLogger(Customer.class.getName());

    /*Para todos os CLientes*/
    @ApiOperation(value ="Bring all Clients")
    @GetMapping
    public List<Customer> getAllClients() {
        logger.info("Returning all costumer");
        return clientService.getAllClients();
    }

    @ApiOperation(value = "Bring a specific client")
    @GetMapping("/{cpf}")
    public Optional<Customer> getClient(@PathVariable @Valid String cpf) {
        logger.info("Returning a specific costumer");
        return Optional.ofNullable(clientService.getClientCpf(cpf));
    }

    /* Registro do Cliente */
    @ApiOperation(value = "Customer Register")
    @PostMapping
    public ResponseEntity<Customer> registerClient (@RequestBody @Valid ClientRequest clientRequest) {
        Customer customer = clientService.registerClient(clientRequest);
        logger.info("Costumer registered");
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    /* Atualizar o Cliente */
    @ApiOperation(value = "Customer Update")
    @PutMapping("/update")
    public ResponseEntity<Customer> updateClient (@RequestBody @Valid ClientRequest cpf) {
        Customer customer = clientService.updateClient(cpf);
        logger.info("Costumer updated");
        return new ResponseEntity<Customer>(customer, HttpStatus.ACCEPTED);
    }

    /* Deletar o Cliente */
    @ApiOperation(value = "Deleting Customer")
    @Transactional
    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deleteClient(@PathVariable String cpf) {
        logger.info("Costumer deleted");
        clientService.deleteClient(cpf);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
