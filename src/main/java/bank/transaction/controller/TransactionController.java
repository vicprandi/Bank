package bank.transaction.controller;


import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.service.TransactionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public List<Transaction> getAllTransactions() {
        logger.info("Returning all transactions");
        return transactionService.getAllTransactions();
    }

    @ApiOperation(value ="Bring transaction by ClientId")
    @GetMapping("/{id}")
    public List<Transaction> findTransactionByClientId(@PathVariable Long id) {
        logger.info("Returning transaction by ClientId");
        List<Transaction> transactions = transactionService.findTransactionByClientId(id);
        return ResponseEntity.ok(transactions).getBody();
    }

    @ApiOperation(value ="Depositar o dinheiro")
    @PostMapping("/deposit/{accountNumber}")
    public Transaction depositMoney(@PathVariable (value = "accountNumber") Long accountNumber, @RequestParam BigDecimal amount) {
        logger.info("Depositing money");
        Transaction transaction = transactionService.depositMoney(accountNumber, amount);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Sacar o dinheiro")
    @PostMapping("/withdraw/{accountNumber}")
    public Transaction withdrawMoney(@PathVariable (value = "accountNumber")  Long accountNumber, @RequestParam BigDecimal amount) {
        logger.info("Withdrawing money");
        Transaction transaction = transactionService.withdrawMoney(accountNumber, amount);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value ="Transferencia entre contas")
    @PostMapping("/transfer")
    public ResponseEntity<List<Transaction>> transferMoney (@RequestParam BigDecimal amount, @RequestParam Long originAccountNumber, @RequestParam Long destinationAccountNumber ) {
        logger.info("Transfering money between accounts");
        List<Transaction> transaction = transactionService.transferMoney(amount, originAccountNumber, destinationAccountNumber);

        return ResponseEntity.ok(transaction);
    }
}