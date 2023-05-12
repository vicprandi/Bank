package bank.customer.controller;


import bank.customer.service.CustomerServiceImpl;
import bank.model.Customer;
import bank.customer.request.CustomerRequest;

import bank.security.exceptions.CustomAuthorizationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public List<Customer> getAllCustomer() {
        logger.info("Returning all costumer");
        return customerService.getAllCustomers();
    }

    @ApiOperation(value = "Bring a specific customer")
    @GetMapping("/{cpf}")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public Optional<Customer> getCustomer(@PathVariable @Valid String cpf) {
        logger.info("Returning a specific costumer");
        return Optional.ofNullable(customerService.getCustomerCpf(cpf));
    }

    /* Registro do Customer */
    @ApiOperation(value = "Customer Register")
    @PostMapping
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public ResponseEntity<Customer> registerCustomer(@RequestBody @Valid CustomerRequest customerRequest) {

        Customer customer = customerService.registerCustomer(customerRequest);
        logger.info("Costumer registered");
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    /* Atualizar o Customer */
    @ApiOperation(value = "Customer Update")
    @PutMapping("/update")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_user')")
    public ResponseEntity<Customer> updateCustomer (@RequestBody @Valid CustomerRequest cpf) {

        Customer customer = customerService.updateCustomer(cpf);
        logger.info("Costumer updated");
        return new ResponseEntity<Customer>(customer, HttpStatus.ACCEPTED);
    }

    /* Deletar o Customer */
    @ApiOperation(value = "Deleting Customer")
    @Transactional
    @DeleteMapping("/{cpf}")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String cpf) {

        logger.info("Costumer deleted");
        customerService.deleteCustomer(cpf);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}