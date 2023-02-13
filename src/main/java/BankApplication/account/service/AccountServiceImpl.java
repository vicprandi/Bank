package BankApplication.account.service;

import BankApplication.client.service.ClientServiceImpl;
import BankApplication.account.exceptions.AccountAlreadyExistsException;
import BankApplication.account.exceptions.AccountDoesntExistException;
import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.account.repository.AccountRepository;
import BankApplication.client.repository.ClientRepository;
import BankApplication.account.request.AccountRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final ClientRepository clientRepository;

    public ClientServiceImpl clientService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    /* Registrar uma conta */
    public Account registerAccount(AccountRequest accountRequest, String cpf) {
        Optional<Client> client  = clientRepository.findByCpf(cpf);

        Long accountNumber = accountRepository.generateAccountNumber();

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountAlreadyExistsException("Account already registred");
        }
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setClient(client.get());
        account.setBalanceMoney(accountRequest.getBalanceMoney());

        return accountRepository.save(account);
    }


    /* Deletar uma Conta */
    public void deleteAccount(@Valid Long id) {
        if (!accountRepository.existsById(id)) throw new AccountDoesntExistException("Account doesn't exists!");

        accountRepository.deleteById(id);
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
    public Long findByAccountNumberByClientId(Long id) throws RuntimeException {
        Optional<Client> client = clientRepository.findById(id);
        Long accountNumber = findByAccountNumberByClientId(client.get().getId());

        if (accountNumber == null) {
            throw new AccountDoesntExistException("Conta não existe!");
        }
        return accountNumber;
    }

    public Optional<Account> getAccountById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        Account account = client.get().getAccount();

        if (account == null) throw new AccountDoesntExistException("Não há conta.");

        return Optional.of(account);
    }

//    /* Usando o cliente, preciso realizar o saque. */
//    public Account withdrawMoney(AccountRequest accountRequest) throws RuntimeException {
//
//        Account account = accountRepository.getReferenceById(accountRequest.getBalanceMoney());
//        Long balance = account.getAmount();
//
//        if (balance < accountRequest.getBalanceMoney()) throw new RuntimeException("Dinheiro insuficiente");
//
//        balance -= accountRequest.getBalanceMoney();
//        account.setAmount(balance);
//
//        return accountRepository.save(account);
//    }
//
//    /* Usando o cliente, preciso ver o saldo. */
//    public Long showBalance (AccountRequest accountRequest) {
//        Account account = accountRepository.getReferenceById(accountRequest.getBalanceMoney());
//        Long balance = account.getAmount();
//        balance+= accountRequest.getBalanceMoney();
//        account.setAmount(balance);
//
//        if (balance < accountRequest.getBalanceMoney()) throw new RuntimeException("Não há saldo suficiente");
//
//        return balance;
//    }
//
//    /* Usando o cliente, preciso realizar transferência para outra conta. Na transferência, o saldo (amount) deve ser suficiente. Na transferência, as contas (accountNumber) devem ser válidas e diferentes. */
//    public String transferMoney(Account originAccount, Account destinationAccount, Long amount) {
//
//        /* Primeira regra: Precisa existir o account number */
//        if (originAccount.getAccountNumber() < 0 || destinationAccount.getAccountNumber() < 0) throw new RuntimeException("Conta inexistente");
//
//        /* Segunda regra: Ter saldo na conta que vai sair o dinheiro*/
//        if (originAccount.getBalanceMoney() < amount) throw new RuntimeException("Saldo insuficiente");
//
//        /*Terceira regra: Contas não podem ser iguais*/
//        if (Objects.equals(originAccount.getClient().getCpf(), destinationAccount.getClient().getCpf())) throw new RuntimeException("Contas iguais!");
//
//        /* Agora sim, a transferência entre contas. */
//        originAccount.setBalanceMoney(originAccount.getBalanceMoney() - amount) ;
//        destinationAccount.setBalanceMoney(destinationAccount.getBalanceMoney() + amount);
//
//        accountRepository.save(originAccount);
//        accountRepository.save(destinationAccount);
//
//        return "Transferência realizada com sucesso";
//    }

    /* Usando o cliente, preciso observar o extrato. No extrato, deve mostrar as trasnferências, depósitos e saques. */

}
