package bank.account.service;

import bank.client.exceptions.CpfDoesntExistException;
import bank.client.service.ClientServiceImpl;
import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.exceptions.AccountDoesntExistException;
import bank.model.Account;
import bank.model.Client;
import bank.account.repository.AccountRepository;
import bank.client.repository.ClientRepository;
import bank.account.request.AccountRequest;

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
    public Account registerAccount(String cpf) {
        Optional<Client> client  = clientRepository.findByCpf(cpf);

        Long accountNumber = accountRepository.generateAccountNumber();

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountAlreadyExistsException("Conta já registrada");
        }

        if (clientRepository.existsByCpf(cpf) == null) {
            throw new CpfDoesntExistException("Cpf não existe");
        }


        AccountRequest accountRequest = new AccountRequest();

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
    public Long findAccountNumberByClientId(Long id) throws RuntimeException {
        Optional<Client> client = clientRepository.findById(id);
        Long accountNumber = client.get().getAccount().getAccountNumber();

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

}
