package BankApplication.controller;

import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.repository.ClientRepository;
import BankApplication.requests.AccountRequest;
import BankApplication.service.AccountServiceImpl;
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

import static org.springframework.http.RequestEntity.get;

@RestController
@RequestMapping("/accounts")
@Api(value = "account")
public class AccountController {

    private final ClientRepository clientRepository;

    private final ClientServiceImpl clientService;
    private final AccountServiceImpl accountService;

    @Autowired
    public AccountController(ClientRepository clientRepository, ClientServiceImpl clientService, AccountServiceImpl accountService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
        this.accountService = accountService;
    }
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /*Para todas as Contas*/
    @ApiOperation(value ="Bring all Accounts")
    @GetMapping
    public List<Account> getAllAcounts() {
        logger.info("Retornando todos as contas existentes");
        return accountService.getAllAccounts();
    }

    /* Registro da Conta */
    @ApiOperation(value = "Account Register")
    @PostMapping("/{cpf}")
    public ResponseEntity<Account> registerAccount (@RequestBody @Valid AccountRequest accountRequest, @PathVariable String cpf) {
        logger.info("Conta registrada");
        Account response = accountService.registerAccount(accountRequest, cpf);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /* Deletar a Conta */
    @ApiOperation(value = "Deleting Account")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        logger.info("Conta deletada");
        accountService.deleteAccount(id);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}