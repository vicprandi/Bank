package BankApplication.transaction.service;

import BankApplication.account.exceptions.AccountAlreadyExistsException;
import BankApplication.account.repository.AccountRepository;
import BankApplication.account.service.AccountServiceImpl;
import BankApplication.client.service.ClientServiceImpl;
import BankApplication.model.Account;
import BankApplication.model.Transaction;
import BankApplication.transaction.exception.ValueNotAcceptedException;
import BankApplication.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    public AccountServiceImpl accountService;

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
    public Transaction findTransactionByClientId(Long id) {
        Optional<Account> account = accountService.getAccountById(id);

        return (Transaction) account.get().getAccountTransaction();

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

    public Transaction transferMoney(BigDecimal amount, Account originAccount, Account destinationAccount) {
        if (originAccount == destinationAccount) throw new AccountAlreadyExistsException("Contas iguais");

        BigDecimal originBalance = originAccount.getBalanceMoney();
        BigDecimal destinationBalance = destinationAccount.getBalanceMoney();

        BigDecimal zero = BigDecimal.valueOf(0);
        if (amount.compareTo(zero) < 0) throw new ValueNotAcceptedException("Valor não aceito");

        if (originBalance.compareTo(zero) < 0) {
            throw new ValueNotAcceptedException("Valor não aceito");
        } else {
            originAccount.setBalanceMoney(originBalance.subtract(amount));
            destinationAccount.setBalanceMoney(destinationBalance.add(amount));
        }

        accountRepository.save(destinationAccount.getClient().getAccount());
        accountRepository.save(originAccount.getClient().getAccount());

        return (Transaction) originAccount.getAccountTransaction();
    }
}
