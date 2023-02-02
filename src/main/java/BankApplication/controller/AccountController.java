package BankApplication.controller;

import BankApplication.model.Account;
import BankApplication.requests.AccountRequest;
import BankApplication.service.AccountServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/accounts")
@Api(value = "account")
public class AccountController {

    private final AccountServiceImpl accountService;

    @Autowired
    public AccountController(AccountServiceImpl accountService) {
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
    @PostMapping
    public ResponseEntity<Account> registerAccount (@RequestBody @Valid AccountRequest accountRequest) {
        Account account = accountService.registerAccount(accountRequest);
        logger.info("Conta registrada");
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    /* Update na Conta */
    @ApiOperation(value ="Account Update")
    @PutMapping("/update")
    public ResponseEntity<Account> updateAccount (@RequestBody @Valid AccountRequest accountRequest) {
        Account account = accountService.updateAccount(accountRequest);
        logger.info("Conta atualizada");
        return new ResponseEntity<>(account, HttpStatus.ACCEPTED);
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
