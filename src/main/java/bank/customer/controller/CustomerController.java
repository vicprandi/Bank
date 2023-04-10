package bank.customer.controller;


import bank.customer.service.CustomerServiceImpl;
import bank.model.Customer;
import bank.customer.request.CustomerRequest;

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
@RequestMapping("/customer")
@Api(value = "customer")
public class CustomerController {
    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }
    private static final Logger logger = Logger.getLogger(Customer.class.getName());

    /*Para todos os CLientes*/
    @ApiOperation(value ="Bring all Customer")
    @GetMapping
    public List<Customer> getAllCustomer() {
        logger.info("Returning all costumer");
        return customerService.getAllCustomers();
    }

    @ApiOperation(value = "Bring a specific customer")
    @GetMapping("/{cpf}")
    public Optional<Customer> getClient(@PathVariable @Valid String cpf) {
        logger.info("Returning a specific costumer");
        return Optional.ofNullable(customerService.getCustomerCpf(cpf));
    }

    /* Registro do Cliente */
    @ApiOperation(value = "Customer Register")
    @PostMapping
    public ResponseEntity<Customer> registerClient (@RequestBody @Valid CustomerRequest customerRequest) {
        Customer customer = customerService.registerCustomer(customerRequest);
        logger.info("Costumer registered");
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    /* Atualizar o Cliente */
    @ApiOperation(value = "Customer Update")
    @PutMapping("/update")
    public ResponseEntity<Customer> updateClient (@RequestBody @Valid CustomerRequest cpf) {
        Customer customer = customerService.updateCustomer(cpf);
        logger.info("Costumer updated");
        return new ResponseEntity<Customer>(customer, HttpStatus.ACCEPTED);
    }

    /* Deletar o Cliente */
    @ApiOperation(value = "Deleting Customer")
    @Transactional
    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deleteClient(@PathVariable String cpf) {
        logger.info("Costumer deleted");
        customerService.deleteCustomer(cpf);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
