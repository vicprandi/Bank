package bank.transaction.service;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.service.AccountServiceImpl;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.service.CustomerServiceImpl;
import bank.kafka.TransferStatus;
import bank.kafka.consumer.TransferMoneyListenerInterface;
import bank.kafka.model.Event;
import bank.kafka.model.EventDTO;
import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import bank.transaction.request.TransactionRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    Logger logger = LoggerFactory.getLogger(getClass());
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    @Autowired
    public AccountServiceImpl accountService;
    @Autowired
    public CustomerServiceImpl customerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, KafkaTemplate<String, EventDTO> kafkaTemplate) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) throw new RuntimeException("There's no transactions");

        return transactions;
    }

    @Override
    public List<Transaction> findTransactionByCustomerId(Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new CustomerDoesntExistException("Customer inexistente"));
        List<Transaction> transactions = account.getAccountTransaction();
        // Envia uma mensagem para o tópico "client-transactions" com a lista de transações encontradas
        return account.getAccountTransaction();
    }

    /* Regras de Negócio: O saldo (balanceMoney) não pode ficar negativo. */
    /* Usando o account number, preciso realizar depósito. */
    public Transaction depositMoney(@Valid TransactionRequest transactionRequest) {

        Account account = accountRepository.findByAccountNumber(transactionRequest.getOriginAccount().getAccountNumber());
        Transaction transaction = transactionRequest.transactionObjectRequest();
        BigDecimal balanceMoney = account.getBalanceMoney();

        BigDecimal zero = BigDecimal.valueOf(0);
        validateAmount(transactionRequest.getValue(), account);

        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        } else account.setBalanceMoney(balanceMoney.add(transactionRequest.getValue()));

        transaction.setTransactionType(Transaction.TransactionEnum.DEPOSIT);
        transaction.setOriginAccount(account);

        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    public Transaction withdrawMoney(@Valid TransactionRequest transactionRequest) {
        Account account = transactionRequest.getOriginAccount();
        Transaction transaction = transactionRequest.transactionObjectRequest();
        BigDecimal value = transactionRequest.getValue();
        BigDecimal balanceMoney = account.getBalanceMoney();
        BigDecimal zero = BigDecimal.valueOf(0);

        validateAmount(value, account);

        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        } else {
            account.setBalanceMoney(balanceMoney.subtract(value));
            transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
            transaction.setOriginAccount(account);

            accountRepository.save(account);

            return transactionRepository.save(transaction);
        }
    }
    @Transactional
    public Long transfer(BigDecimal amount, Long originAccountNumber, Long destinationAccountNumber) {
        Account originAccount = accountRepository.findByAccountNumber(originAccountNumber);
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);

        logger.info("Validating accounts...");
        validateAccounts(originAccount, destinationAccount);
        logger.info("Validating amount...");
        validateAmount(amount, originAccount);

        logger.info("Creating transactions...");
        Transaction transaction = createTransaction(amount, originAccount, destinationAccount, Transaction.TransactionEnum.TRANSFER);

        try {
            logger.info("Saving transactions...");
            saveTransactions(transaction);

            logger.info("Sending event to Kafka...");
            EventDTO event = new EventDTO(Event.SAVE_TRANSFER,
                    amount,
                    transaction.getId(),
                    TransferStatus.PENDING);
            kafkaTemplate.send("transactions", event);

        } catch (Exception e) {
            throw new RuntimeException("Can't make the transfer");
        }

        return transaction.getId();
    }

    private void validateAccounts(Account originAccount, Account destinationAccount) {
        if (Objects.equals(originAccount.getAccountNumber(), destinationAccount.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Same accounts");
        }
    }

    private void validateAmount(BigDecimal amount, Account originAccount) {
        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) <= 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        }
        if (originAccount.getBalanceMoney().compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Value not accepted");
        }
    }

    private Transaction createTransaction(BigDecimal amount, Account account, Account destinationAccount, Transaction.TransactionEnum transactionType) {
        Transaction transaction = new Transaction();
        transaction.setValue(amount);
        transaction.setOriginAccount(account);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTransactionType(transactionType);

        return transaction;
    }

    public void saveTransactions(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Transactional
    public void executeTransfer(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);

        if (transaction.isPresent()) {
            Transaction originTransfer = transaction.get();

            try {
                originTransfer.setTransactionType(Transaction.TransactionEnum.TRANSFER);
                transactionRepository.save(originTransfer);

                // Lógica do método updateAccounts movida para dentro de executeTransfer
                BigDecimal value = originTransfer.getValue();
                Account originAccount = originTransfer.getOriginAccount();
                Account destinationAccount = originTransfer.getDestinationAccount();

                BigDecimal originBalance = originAccount.getBalanceMoney();
                BigDecimal destinationBalance = destinationAccount.getBalanceMoney();

                originAccount.setBalanceMoney(originBalance.subtract(value));
                destinationAccount.setBalanceMoney(destinationBalance.add(value));

                List<Account> accounts = Arrays.asList(originAccount, destinationAccount);
                accountRepository.saveAll(accounts);

                logger.info("Money transfer completed!");

            } catch (Exception e) {
                throw new RuntimeException("Can't execute the transfer");
            }
        } else {
            throw new RuntimeException("Origin transaction not found");
        }
    }
    public Optional<Transaction> findById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public Optional<Transaction> findByOriginAccount(Long transactionAccountNumber) {
        return transactionRepository.findByOriginAccount(transactionAccountNumber);
    }

}
