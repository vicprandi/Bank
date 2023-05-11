package bank.transaction.controller;

import bank.kafka.consumer.TransferMoneyListener;
import bank.kafka.producer.KafkaService;
import bank.model.Account;
import bank.model.Transaction;
import bank.security.exceptions.CustomAuthorizationException;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.request.TransactionRequest;
import bank.transaction.service.TransactionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/transaction")
@Api(value = "transaction")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    private final TransactionRepository transactionRepository;

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Autowired
    private TransferMoneyListener transferMoneyListener;
    @Autowired
    private KafkaService kafkaService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService, TransactionRepository transactionRepository) {

        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
    }
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    @ApiOperation(value ="Bring all Transactions")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public List<Transaction> getAllTransactions() {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();

        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Returning all transactions");
        return transactionService.getAllTransactions();
    }

    @ApiOperation(value ="Bring transaction by CustomerId")
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public List<Transaction> findTransactionByClientId(@PathVariable Long id) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();

        boolean hasAdminScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin"));

        if (!hasAdminScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Returning transaction by ClientId");
        List<Transaction> transactions = transactionService.findTransactionByCustomerId(id);
        return ResponseEntity.ok(transactions).getBody();
    }

    @ApiOperation(value ="Depositar o dinheiro")
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasAuthority('SCOPE_user')")
    public Transaction depositMoney(@RequestBody @Valid TransactionRequest transactionRequest) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin") || authority.getAuthority().equals("SCOPE_user"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Depositing money");
        Transaction transaction = transactionService.depositMoney(transactionRequest);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Sacar o dinheiro")
    @PostMapping("/withdraw/{accountNumber}")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasAuthority('SCOPE_user')")
    public Transaction withdrawMoney(@RequestBody @Valid TransactionRequest transactionRequest) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin") || authority.getAuthority().equals("SCOPE_user"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Withdrawing money");
        Transaction transaction = transactionService.withdrawMoney(transactionRequest);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Transferencia entre contas")
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasAuthority('SCOPE_user')")
    public ResponseEntity<Long> transferMoney (@RequestParam BigDecimal amount, @RequestParam Long originAccountNumber, @RequestParam Long destinationAccountNumber) throws InterruptedException {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin") || authority.getAuthority().equals("SCOPE_user"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        logger.info("Transfering money between accounts");
        Long transactionId = transactionService.transfer(amount, originAccountNumber, destinationAccountNumber);

        return ResponseEntity.ok(transactionId);
    }

    // Adicione um novo endpoint GET para buscar a transação pelo ID
    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasAuthority('SCOPE_user')")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        // Verificar se o usuário tem o escopo
        Authentication authentication = getCurrentAuthentication();
        boolean hasValidScope = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_admin") || authority.getAuthority().equals("SCOPE_user"));

        if (!hasValidScope) {
            throw new CustomAuthorizationException("Acesso negado");
        }

        Optional<Transaction> transaction = transactionService.findById(transactionId);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}