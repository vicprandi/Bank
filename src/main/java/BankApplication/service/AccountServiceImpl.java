package BankApplication.service;

import BankApplication.exception.AccountAlreadyExistsException;
import BankApplication.exception.AccountDoesntExistException;
import BankApplication.model.Account;
import BankApplication.repository.AccountRepository;
import BankApplication.requests.AccountRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /* Trazer todas as Contas */
    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();

        if (accounts.isEmpty()) throw new AccountDoesntExistException("Não há contas.");
        return accounts;
    }

    /* Achar conta pelo AccountNumber */
    @Override
    public Account findByAccountNumber(Long accountNumber) {

        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if (account.isEmpty()) {
            throw new AccountDoesntExistException("Conta não existe!");
        }
        return account.get();
    }

    /* Registrar uma conta */
    public Account registerAccount(@Valid AccountRequest accountRequest) {

        Account account = accountRequest.requestAccount();

        if (accountRepository.existsByAccountNumber(accountRequest.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account already registred");
        }

        return accountRepository.save(account);
    }

    /* Deletar uma Conta */
    public void deleteAccount(@Valid Long id) {
        if (!accountRepository.existsById(id)) throw new AccountDoesntExistException("Conta não existe");

        accountRepository.deleteById(id);
    }

    /* Dar o update numa Conta */
    public Account updateAccount(@NotNull AccountRequest accountRequest) {

        Account account = findByAccountNumber(accountRequest.getAccountNumber());

        account.setBalanceMoney(accountRequest.getBalanceMoney());
        account.setAmount(accountRequest.getAmount());

        return accountRepository.save(account);
    }

    /* Regras de Negócio: O saldo (amount) não pode ficar negativo. */
    /* Usando o cliente, preciso realizar depósito. */
    public Account depositMoney(AccountRequest accountRequest) {
        Account account = accountRepository.getReferenceById(accountRequest.getBalanceMoney());

        Long balance = account.getAmount();
        balance+= accountRequest.getBalanceMoney();
        account.setAmount(balance);

        return accountRepository.save(account);
    }

    /* Usando o cliente, preciso realizar o saque. */
    public Account withdrawMoney(AccountRequest accountRequest) throws RuntimeException {

        Account account = accountRepository.getReferenceById(accountRequest.getBalanceMoney());
        Long balance = account.getAmount();

        if (balance < accountRequest.getBalanceMoney()) throw new RuntimeException("Dinheiro insuficiente");

        balance -= accountRequest.getBalanceMoney();
        account.setAmount(balance);

        return accountRepository.save(account);
    }

    /* Usando o cliente, preciso ver o saldo. */
    public Long showBalance (AccountRequest accountRequest) {
        Account account = accountRepository.getReferenceById(accountRequest.getBalanceMoney());
        Long balance = account.getAmount();
        balance+= accountRequest.getBalanceMoney();
        account.setAmount(balance);

        if (balance < accountRequest.getBalanceMoney()) throw new RuntimeException("Não há saldo suficiente");

        return balance;
    }

    /* Usando o cliente, preciso realizar transferência para outra conta. Na transferência, o saldo (amount) deve ser suficiente. Na transferência, as contas (accountNumber) devem ser válidas e diferentes. */
    public String transferMoney(Account originAccount, Account destinationAccount, Long amount) {

        /* Primeira regra: Precisa existir o account number */
        if (originAccount.getAccountNumber() < 0 || destinationAccount.getAccountNumber() < 0) throw new RuntimeException("Conta inexistente");

        /* Segunda regra: Ter saldo na conta que vai sair o dinheiro*/
        if (originAccount.getBalanceMoney() < amount) throw new RuntimeException("Saldo insuficiente");

        /*Terceira regra: Contas não podem ser iguais*/
        if (Objects.equals(originAccount.getClient().getCpf(), destinationAccount.getClient().getCpf())) throw new RuntimeException("Contas iguais!");

        /* Agora sim, a transferência entre contas. */
        originAccount.setBalanceMoney(originAccount.getBalanceMoney() - amount) ;
        destinationAccount.setBalanceMoney(destinationAccount.getBalanceMoney() + amount);

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        return "Transferência realizada com sucesso";
    }

    /* Usando o cliente, preciso observar o extrato. No extrato, deve mostrar as trasnferências, depósitos e saques. */
}
