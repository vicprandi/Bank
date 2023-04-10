package bank.transaction.controller;

import bank.kafka.model.EventDTO;
import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.service.TransactionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    @ApiOperation(value = "Bring all Transactions")
    @GetMapping
    public List<Transaction> getAllTransactions() {
        logger.info("Returning all transactions");
        return transactionService.getAllTransactions();
    }

    @ApiOperation(value = "Bring transaction by ClientId")
    @GetMapping("/{id}")
    public List<Transaction> findTransactionByClientId(@PathVariable Long id) {
        logger.info("Returning transaction by ClientId");
        List<Transaction> transactions = transactionService.findTransactionByClientId(id);
        return ResponseEntity.ok(transactions).getBody();
    }

    @ApiOperation(value = "Depositing money")
    @PostMapping("/deposit/{accountNumber}")
    public Transaction depositMoney(@PathVariable(value = "accountNumber") Long accountNumber, @RequestParam BigDecimal amount) {
        logger.info("Depositing money");
        Transaction transaction = transactionService.depositMoney(accountNumber, amount);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value = "Withdraw money")
    @PostMapping("/withdraw/{accountNumber}")
    public Transaction withdrawMoney(@PathVariable(value = "accountNumber") Long accountNumber, @RequestParam BigDecimal amount) {
        logger.info("Withdrawing money");
        Transaction transaction = transactionService.withdrawMoney(accountNumber, amount);
        return ResponseEntity.ok(transaction).getBody();
    }

    @ApiOperation(value = "Transferring between accounts")
    @PostMapping("/transfer")
    // O retorno é uma CompletableFuture com ResponseEntity que pode conter informações sobre o resultado da operação.
    public CompletableFuture<ResponseEntity<?>> transferMoney(@RequestParam BigDecimal amount, @RequestParam Long originAccountNumber, @RequestParam Long destinationAccountNumber) {
        logger.info("Transferring money between accounts");

        EventDTO event = new EventDTO();
        event.setAmount(amount);
        event.setOriginAccount(String.valueOf(originAccountNumber));
        event.setRecipientAccount(String.valueOf(destinationAccountNumber));

    // Executa um método assíncrono processEvent() do serviço de transação que retorna uma CompletableFuture com uma lista de transações.
    // Quando a operação for concluída com sucesso, o resultado é processado no método thenApply(), que retorna uma ResponseEntity vazia com status 200 OK e uma mensagem de log é registrada.
        CompletableFuture<List<Transaction>> future = (CompletableFuture<List<Transaction>>) transactionService.processEvent(event);

        return future.thenApply(transactions -> {
            logger.info("Money transferred.");
            return ResponseEntity.ok().build();
        });
    }

    @ApiOperation(value ="Getting transaction information after Kafka")
    @GetMapping("/getTransfer/{transactionId}")
   //Retorna uma CompletableFuture que contém uma ResponseEntity com o resultado da busca da transação.
   // O registro de uma mensagem de log informando que a transação está sendo recuperada é feito antes da operação.
    public CompletableFuture<ResponseEntity<Transaction>> getTransaction(@PathVariable String transactionId) {
        logger.info("Retrieving transaction.");
        Transaction transaction = transactionService.findTransactionByTransactionId(Long.valueOf(transactionId));
        ResponseEntity<Transaction> responseEntity = ResponseEntity.ok(transaction);
        return CompletableFuture.completedFuture(responseEntity);
    }
}