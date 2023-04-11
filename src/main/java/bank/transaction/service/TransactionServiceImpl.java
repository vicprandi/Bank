package bank.transaction.service;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.service.AccountServiceImpl;
import bank.customer.exceptions.CustomerDoesntExistException;
import bank.customer.service.CustomerServiceImpl;
import bank.model.Account;
import bank.model.Transaction;
import bank.transaction.exception.ValueNotAcceptedException;
import bank.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, EventDTO> kafkaTemplate;
    @Autowired
    public AccountServiceImpl accountService;
    @Autowired
    public CustomerServiceImpl customerService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, KafkaTemplate<String, EventDTO> kafkaTemplate ) {
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
                .orElseThrow(() -> new CustomerDoesntExistException("Customere inexistente"));
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
    @Transactional
    public List<Transaction> transferMoney(BigDecimal amount, Long originAccountNumber, Long destinationAccountNumber, TransferMoneyListener listener) {
        Account originAccount = accountRepository.findByAccountNumber(originAccountNumber);
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);

        validateAccounts(originAccount, destinationAccount);
        validateAmount(amount, originAccount);

        Transaction originTransaction = createTransaction(amount, originAccount, Transaction.TransactionEnum.TRANSFER);
        Transaction destinationTransaction = createTransaction(amount, destinationAccount, Transaction.TransactionEnum.TRANSFER);

        try {
            saveTransactions(originTransaction, destinationTransaction);
            updateAccounts(originAccount, destinationAccount, amount);

            EventDTO event = new EventDTO(Event.SAVE_TRANSFER, amount, originAccountNumber.toString(), destinationAccountNumber.toString(), TransferStatus.SUCCESSFUL);
            kafkaTemplate.send("transactions", event);

            listener.onMoneyTransfer(); // notifica o listener de que a transferência foi realizada

        } catch (Exception e) {
            throw new RuntimeException("Can't make the transfer");
        }

        return Arrays.asList(originTransaction, destinationTransaction);
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

    private Transaction createTransaction(BigDecimal amount, Account account, Transaction.TransactionEnum transactionType) {
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
