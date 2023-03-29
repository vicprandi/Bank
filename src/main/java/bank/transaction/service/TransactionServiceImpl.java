package bank.transaction.service;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.service.AccountServiceImpl;
import bank.customer.exceptions.ClientDoesntExistException;
import bank.customer.service.CustomerServiceImpl;
import bank.kafka.TransferStatus;
import bank.kafka.model.EventDTO;
import bank.kafka.producer.KafkaService;
import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.exception.TransferValidationException;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

        if (transactions.isEmpty()) throw new RuntimeException("There's no transactions");

        return transactions;
    }

    @Override
    public List<Transaction> findTransactionByClientId(Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new ClientDoesntExistException("Cliente doesnt exist"));
        List<Transaction> transactions = account.getAccountTransaction();

        // Envia uma mensagem para o tópico "client-transactions" com a lista de transações encontradas
        return account.getAccountTransaction();
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
public List<Transaction> processEvent(EventDTO event) {
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

        logger.info("Message processed sucessfully!");

        event.setStatus(TransferStatus.SUCCESSFUL);
        return Arrays.asList(originTransaction, destinationTransaction);
    } catch (TransferValidationException ex) {
        event.setStatus(TransferStatus.FAILED);
        logger.error("Error processing event.", ex);
        return Collections.emptyList();
    }
}
//  Foi colocado no TransferMoneyListener:
//    private List<Transaction> consumeTransactionFromKafka() {
//        List<Transaction> transactions = new ArrayList<>();
//
//        Consumer<String, EventDTO> consumer = consumerFactory.createConsumer();
//        consumer.subscribe(Collections.singleton("transactions"));
//
//        while (true) {
//            ConsumerRecords<String, EventDTO> records = consumer.poll(Duration.ofMillis(100));
//
//            for (ConsumerRecord<String, EventDTO> record : records) {
//                String message = String.valueOf(record.value());
//                logger.info("Received message from Kafka: {}", message);
//
//                EventDTO event = record.value();
//                processEvent(event);
//                // Salva a transação da conta de origem
//                transactions.add(createTransaction(event.getAmount(), accountRepository.findByAccountNumber(Long.valueOf(event.getOriginAccount())), Transaction.TransactionEnum.TRANSFER));
//            }
//            consumer.commitSync();
//        }
//    }

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
        transactionRepository.save(originTransaction);
        transactionRepository.save(destinationTransaction);
    }

    private void updateAccounts(Account originAccount, Account destinationAccount, BigDecimal amount) {
        originAccount.setBalanceMoney(originAccount.getBalanceMoney().subtract(amount));
        destinationAccount.setBalanceMoney(destinationAccount.getBalanceMoney().add(amount));
        accountRepository.saveAll(Arrays.asList(originAccount, destinationAccount));
    }
}
