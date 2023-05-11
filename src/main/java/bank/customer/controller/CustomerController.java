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

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /*Para todos os CLientes*/
    @ApiOperation(value ="Bring all Customer")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public List<Customer> getAllCustomer() {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();

        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Returning all costumer");
        return customerService.getAllCustomers();
    }

    @ApiOperation(value = "Bring a specific customer")
    @GetMapping("/{cpf}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Optional<Customer> getCustomer(@PathVariable @Valid String cpf) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Returning a specific costumer");
        return Optional.ofNullable(customerService.getCustomerCpf(cpf));
    }

    /* Registro do Customer */
    @ApiOperation(value = "Customer Register")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Customer> registerCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        Customer customer = customerService.registerCustomer(customerRequest);
        logger.info("Costumer registered");
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    /* Atualizar o Customer */
    @ApiOperation(value = "Customer Update")
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_user')")
    public ResponseEntity<Customer> updateCustomer (@RequestBody @Valid CustomerRequest cpf) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_user"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        Customer customer = customerService.updateCustomer(cpf);
        logger.info("Costumer updated");
        return new ResponseEntity<Customer>(customer, HttpStatus.ACCEPTED);
    }

    /* Deletar o Customer */
    @ApiOperation(value = "Deleting Customer")
    @Transactional
    @DeleteMapping("/{cpf}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String cpf) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Costumer deleted");
        customerService.deleteCustomer(cpf);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}