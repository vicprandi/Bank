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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/accounts")
@Api(value = "account")
public class AccountController {

    private final AccountServiceImpl accountService;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    public AccountController(AccountServiceImpl accountService) {

        this.accountService = accountService;
    }
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    //Autorização!!!//
    private void checkAuthorization(Long accountId, Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = jwtAuthenticationToken.getToken();
        Long userId = jwt.getClaim("sub");

        // Substitua a lógica abaixo pela verificação real de autorização
        if (!Objects.equals(accountId, userId)) {
            throw new CustomAuthorizationException("Acesso negado");
        }
    }

    /* Para todas as Contas */
    @ApiOperation(value = "Bring all Accounts")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public List<Account> getAllAcounts() {
        // Verificar se o usuário tem o escopo view_all_accounts_admin
        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Returning all accounts");
        return accountService.getAllAccounts();
    }

    /* Para uma conta */
    @ApiOperation(value = "Bring a account")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public Optional<Account> getAccount(@PathVariable Long id) {
        checkAuthorization(id, authentication);
        logger.info("Returning a specific account");
        return accountService.getAccountById(id);
    }

    /* Registro da Conta */
    @ApiOperation(value = "Account Register")
    @PostMapping("/{cpf}")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasAuthority('SCOPE_user')")
    public ResponseEntity<Account> registerAccount(@PathVariable String cpf) {
        // Verificar se o usuário tem o escopo register_account_manager
        authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    authority.getAuthority();
                    return false;
                });
        boolean hasManagerScope = false;

        if (!hasManagerScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Account registered");
        Account response = accountService.registerAccount(cpf);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /* Deletar a Conta */
    @ApiOperation(value = "Deleting Account")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        checkAuthorization(id, authentication);
        logger.info("Account deleted");
        accountService.deleteAccount(id);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}