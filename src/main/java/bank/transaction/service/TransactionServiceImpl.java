package bank.transaction.service;

import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.repository.AccountRepository;
import bank.account.service.AccountServiceImpl;
import bank.customer.exceptions.ClientDoesntExistException;
import bank.customer.service.ClientServiceImpl;
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

    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    @Autowired
    public AccountServiceImpl accountService;
    @Autowired
    public ClientServiceImpl clientService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository,  KafkaTemplate<String, Transaction> kafkaTemplate ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) throw new RuntimeException("There's no transactions");

        kafkaTemplate.send("transactions", (Transaction) transactions);
        return transactions;
    }

    @Override
    public List<Transaction> findTransactionByClientId(Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new ClientDoesntExistException("Cliente inexistente"));
        List<Transaction> transactions = account.getAccountTransaction();

        // Envia uma mensagem para o tópico "client-transactions" com a lista de transações encontradas
        kafkaTemplate.send("transactions", (Transaction) transactions);
        return transactions;
    }

    /* Regras de Negócio: O saldo (balanceMoney) não pode ficar negativo. */
    /* Usando o account number, preciso realizar depósito. */
    public Transaction depositMoney(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        Transaction transaction = new Transaction();
        BigDecimal balanceMoney = account.getBalanceMoney();

        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) < 0) throw new ValueNotAcceptedException("Valor não aceito");

        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        } else account.setBalanceMoney(balanceMoney.add(amount));

        transaction.setTransactionType(Transaction.TransactionEnum.DEPOSIT);
        transaction.setValue(amount);
        transaction.setAccount(account);

        accountRepository.save(account);

        kafkaTemplate.send("transactions", transaction);
        return transaction;
    }

    public Transaction withdrawMoney(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        Transaction transaction = new Transaction();
        BigDecimal balanceMoney = account.getBalanceMoney();
        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) < 0) throw new ValueNotAcceptedException("Valor não aceito");
        if (balanceMoney.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        } else account.setBalanceMoney(balanceMoney.subtract(amount));

        transaction.setTransactionType(Transaction.TransactionEnum.WITHDRAW);
        transaction.setValue(amount);
        transaction.setAccount(account);

        accountRepository.save(account);

        kafkaTemplate.send("transactions", transaction);
        return transaction;
    }

    @Transactional
    public List<Transaction> transferMoney(BigDecimal amount, Long originAccountNumber, Long destinationAccountNumber) {
        Account originAccount = accountRepository.findByAccountNumber(originAccountNumber);
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);

        validateAccounts(originAccount, destinationAccount);
        validateAmount(amount, originAccount);

        Transaction originTransaction = createTransaction(amount, originAccount, Transaction.TransactionEnum.TRANSFER);
        Transaction destinationTransaction = createTransaction(amount, destinationAccount, Transaction.TransactionEnum.TRANSFER);

        try {
            saveTransactions(originTransaction, destinationTransaction);
            updateAccounts(originAccount, destinationAccount, amount);
            kafkaTemplate.send("transactions", (Transaction) Arrays.asList(originTransaction, destinationTransaction));

        } catch (Exception e) {
            throw new RuntimeException("Não foi possível realizar a transferência");
        }

        return Arrays.asList(originTransaction, destinationTransaction);
    }

    private void validateAccounts(Account originAccount, Account destinationAccount) {
        if (Objects.equals(originAccount.getAccountNumber(), destinationAccount.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Contas iguais");
        }
    }

    private void validateAmount(BigDecimal amount, Account originAccount) {
        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) <= 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        }
        if (originAccount.getBalanceMoney().compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        }
    }

    private Transaction createTransaction(BigDecimal amount, Account account, Transaction.TransactionEnum transactionType) {
        Transaction transaction = new Transaction();
        transaction.setValue(amount);
        transaction.setAccount(account);
        transaction.setTransactionType(transactionType);

        kafkaTemplate.send("transactions", transaction);
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
