package bank.account.controller;

import bank.model.Account;
import bank.account.service.AccountServiceImpl;


import bank.security.exceptions.CustomAuthorizationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/accounts")
@Api(value = "account")
public class AccountController {

    private final AccountServiceImpl accountService;


    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Autowired
    public AccountController(AccountServiceImpl accountService) {

        this.accountService = accountService;
    }
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    /* Para todas as Contas */
    @ApiOperation(value = "Bring all Accounts")
    @GetMapping
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public List<Account> getAllAcounts() {

        logger.info("Returning all accounts");
        return accountService.getAllAccounts();
    }

    /* Para uma conta */
    @ApiOperation(value = "Bring a account")
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public Optional<Account> getAccount(@PathVariable Long id) {

        logger.info("Returning a specific account");
        return accountService.getAccountById(id);
    }

    /* Registro da Conta */
    @ApiOperation(value = "Account Register")
    @PostMapping("/{cpf}")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_user')")
    public ResponseEntity<Account> registerAccount(@PathVariable String cpf) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Account registered");
        Account response = accountService.registerAccount(cpf);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /* Deletar a Conta */
    @ApiOperation(value = "Deleting Account")
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpressionRoot.hasScope('SCOPE_admin')")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {

        logger.info("Account deleted");
        accountService.deleteAccount(id);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}