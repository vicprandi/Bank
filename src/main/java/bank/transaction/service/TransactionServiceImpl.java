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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl accountService;
    @Autowired
    public ClientServiceImpl clientService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) throw new RuntimeException("Não há transações");

        return transactions;
    }

    @Override
    public List<Transaction> findTransactionByClientId(Long id) {
        Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new ClientDoesntExistException("Cliente inexistente"));

        return account.getAccountTransaction();

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

        return transactionRepository.save(transaction);
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

        return transactionRepository.save(transaction);
    }

    @Transactional
    public List<Transaction> transferMoney(BigDecimal amount, Long originAccountNumber, Long destinationAccountNumber) {
        Account originAccount = accountRepository.findByAccountNumber(originAccountNumber);
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);

        if (Objects.equals(originAccountNumber, destinationAccountNumber))
            throw new AccountAlreadyExistsException("Contas iguais");

        BigDecimal originBalance = originAccount.getBalanceMoney();
        BigDecimal destinationBalance = destinationAccount.getBalanceMoney();

        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) <= 0) throw new ValueNotAcceptedException("Valor não aceito");

        if (originBalance.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        }

        Transaction originTransaction = new Transaction();
        originTransaction.setValue(amount);
        originTransaction.setAccount(originAccount);
        originTransaction.setTransactionType(Transaction.TransactionEnum.TRANSFER);

        Transaction destinationTransaction = new Transaction();
        destinationTransaction.setValue(amount);
        destinationTransaction.setAccount(destinationAccount);
        destinationTransaction.setTransactionType(Transaction.TransactionEnum.TRANSFER);


        try {
            // start transaction
            transactionRepository.save(originTransaction);
            transactionRepository.save(destinationTransaction);

            originAccount.setBalanceMoney(originBalance.subtract(amount));
            destinationAccount.setBalanceMoney(destinationBalance.add(amount));

            // persist changes to accounts
            accountRepository.save(originAccount);
            accountRepository.save(destinationAccount);

            // commit transaction
        } catch (Exception e) {
            // rollback transaction
            throw new RuntimeException("Não foi possível realizar a transferência");
        }

        return Arrays.asList(originTransaction, destinationTransaction);
    }
}
