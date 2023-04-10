
package bank.transaction.service;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.service.AccountServiceImpl;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.service.CustomerServiceImpl;
import bank.kafka.TransferStatus;
import bank.kafka.model.EventDTO;
import bank.kafka.producer.KafkaService;
import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.exception.TransactionNotFoundException;
import bank.transaction.exception.TransferValidationException;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    @Autowired
    public AccountServiceImpl accountService;
    @Autowired
    public CustomerServiceImpl clientService;
    private final ConsumerFactory<String, EventDTO> consumerFactory;

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;
    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, KafkaTemplate<String, EventDTO> kafkaTemplate, ConsumerFactory<String, EventDTO> consumerFactory ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) throw new TransactionNotFoundException("There's no transactions");

        return transactions;
    }

    @Override
    public List<Transaction> findTransactionByClientId(Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new CustomerDoesntExistException("Cliente doesnt exist"));
        List<Transaction> transactions = account.getAccountTransaction();

        // Envia uma mensagem para o tópico "client-transactions" com a lista de transações encontradas
        return account.getAccountTransaction();
    }

    public Transaction findTransactionByTransactionId(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction doesn't exist"));
    }

    /* Regras de Negócio: O saldo (balanceMoney) não pode ficar negativo. */
    /* Usando o account number, preciso realizar depósito. */
    public Transaction depositMoney(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        Transaction transaction = new Transaction();
        BigDecimal balanceMoney = account.getBalanceMoney();

        BigDecimal zero = BigDecimal.valueOf(0);
        validateAmount(amount, account);

        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        } else account.setBalanceMoney(balanceMoney.add(amount));

        transaction.setTransactionType(Transaction.TransactionEnum.DEPOSIT);
        transaction.setValue(amount);
        transaction.setAccount(account);

        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    public Transaction withdrawMoney(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        Transaction transaction = new Transaction();
        BigDecimal balanceMoney = account.getBalanceMoney();
        BigDecimal zero = BigDecimal.valueOf(0);

        validateAmount(amount, account);

        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        } else account.setBalanceMoney(balanceMoney.subtract(amount));

        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
        transaction.setValue(amount);
        transaction.setAccount(account);

        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }
    public Future<List<Transaction>> processEvent(EventDTO event) {
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();

        Account originAccount = accountRepository.findByAccountNumber(Long.valueOf(event.getOriginAccount()));
        Account destinationAccount = accountRepository.findByAccountNumber(Long.valueOf(event.getRecipientAccount()));
        BigDecimal amount = event.getAmount();

        Transaction originTransaction = createTransaction(amount, originAccount, Transaction.TransactionEnum.TRANSFER);
        Transaction destinationTransaction = createTransaction(amount, destinationAccount, Transaction.TransactionEnum.TRANSFER);

        try {
            validateAccounts(originAccount, destinationAccount);
            validateAmount(amount, originAccount);
            saveTransactions(originTransaction, destinationTransaction);
            updateAccounts(originAccount, destinationAccount, amount);

            logger.info("Message processed successfully!");

            event.setStatus(TransferStatus.SUCCESSFUL);
            future.complete(Arrays.asList(originTransaction, destinationTransaction));
        } catch (TransferValidationException ex) {
            event.setStatus(TransferStatus.FAILED);
            logger.error("Error processing event.", ex);
            future.completeExceptionally(new TransferValidationException("Error processing Event"));
        }
        return future;
    }

    private void validateAccounts(Account originAccount, Account destinationAccount) {
        if (Objects.equals(originAccount.getAccountNumber(), destinationAccount.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Same accounts");
        }
    }

    private void validateAmount(BigDecimal amount, Account originAccount) {
        if (amount.compareTo(ZERO_AMOUNT) <= 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        }
        if (originAccount.getBalanceMoney().compareTo(ZERO_AMOUNT) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        }
    }

    public Transaction createTransaction(BigDecimal amount, Account account, Transaction.TransactionEnum transactionType) {
        Transaction transaction = new Transaction();
        transaction.setValue(amount);
        transaction.setAccount(account);
        transaction.setTransactionType(transactionType);

        return transaction;
    }

    private void saveTransactions(Transaction originTransaction, Transaction destinationTransaction) {
        transactionRepository.saveAll(Arrays.asList(originTransaction, destinationTransaction));
    }

    private void updateAccounts(Account originAccount, Account destinationAccount, BigDecimal amount) {
        originAccount.setBalanceMoney(originAccount.getBalanceMoney().subtract(amount));
        destinationAccount.setBalanceMoney(destinationAccount.getBalanceMoney().add(amount));
        accountRepository.saveAll(Arrays.asList(originAccount, destinationAccount));
    }
}
