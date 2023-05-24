package bank.transaction.controller;


import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.request.TransactionRequest;
import bank.transaction.service.TransactionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {

        this.transactionService = transactionService;
    }
    private static final Logger logger = Logger.getLogger(Account.class.getName());

    @ApiOperation(value ="Bring all Transactions")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin:view')")
    public List<Transaction> getAllTransactions() {

        logger.info("Returning all transactions");
        return transactionService.getAllTransactions();
    }

    @ApiOperation(value ="Bring transaction by CustomerId")
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin:view')")
    public List<Transaction> findTransactionByClientId(@PathVariable Long id) {

        logger.info("Returning transaction by ClientId");
        List<Transaction> transactions = transactionService.findTransactionByCustomerId(id);
        return ResponseEntity.ok(transactions).getBody();
    }

    @ApiOperation(value ="Depositar o dinheiro")
    @PostMapping("/deposit")
    @PreAuthorize("hasAuthority('SCOPE_user:write')")
    public Transaction depositMoney(@RequestBody @Valid TransactionRequest transactionRequest) {

        logger.info("Depositing money");
        Transaction transaction = transactionService.depositMoney(transactionRequest);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Sacar o dinheiro")
    @PostMapping("/withdraw/{accountNumber}")
    @PreAuthorize("hasAuthority('SCOPE_user:write')")
    public Transaction withdrawMoney(@RequestBody @Valid TransactionRequest transactionRequest) {
        logger.info("Withdrawing money");
        Transaction transaction = transactionService.withdrawMoney(transactionRequest);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Transferencia entre contas")
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('SCOPE_user:write')")
    public ResponseEntity<Long> transferMoney (@RequestParam BigDecimal amount, @RequestParam Long originAccountNumber, @RequestParam Long destinationAccountNumber) throws InterruptedException {

        logger.info("Transfering money between accounts");
        Long transactionId = transactionService.transfer(amount, originAccountNumber, destinationAccountNumber);

        return ResponseEntity.ok(transactionId);
    }

    // Adicione um novo endpoint GET para buscar a transação pelo ID
    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAuthority('SCOPE_admin:view') or hasAuthority('SCOPE_user:view')")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {

        Optional<Transaction> transaction = transactionService.findById(transactionId);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}