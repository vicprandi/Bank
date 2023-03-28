package bank.account.service;

import bank.account.exceptions.CpfDoesntExistException;
import bank.customer.exceptions.ClientDoesntExistException;
import bank.customer.service.CustomerServiceImpl;
import bank.account.exceptions.AccountAlreadyExistsException;
import bank.account.exceptions.AccountDoesntExistException;
import bank.model.Account;
import bank.model.Customer;
import bank.account.repository.AccountRepository;
import bank.customer.repository.CustomerRepository;
import bank.account.request.AccountRequest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl customerService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    /* Registrar uma conta */
    public Account registerAccount(String cpf) {
        Optional<Customer> customer  = customerRepository.findByCpf(cpf);

        Long accountNumber = accountRepository.generateAccountNumber();

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountAlreadyExistsException("Account already registered");
        }

        if (customerRepository.existsByCpf(cpf) == null) {
            throw new CpfDoesntExistException("CPF doesn't exist");
        }

        AccountRequest accountRequest = new AccountRequest();

        Account account = new Account();
        account.setAccountNumber(accountNumber);

        if (customer.isEmpty()) {
            throw new ClientDoesntExistException("Client doesn't exist!");
        } else {
            account.setCustomer(customer.get());
        }

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

        if (accounts.isEmpty()) throw new AccountDoesntExistException("There's no accounts.");
        return accounts;
    }

    /* Achar conta pelo AccountNumber */
    @Override
    public Long findAccountNumberByClientId(Long id) throws RuntimeException {
        Optional<Customer> customer = customerRepository.findById(id);
        Long accountNumber = customer.get().getAccount().getAccountNumber();

        if (accountNumber == null) {
            throw new AccountDoesntExistException("Account doesn't exist!");
        }
        return accountNumber;
    }

    public Optional<Account> getAccountById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        Account account = customer.get().getAccount();

        if (account == null) throw new AccountDoesntExistException("There is no account.");

        return Optional.of(account);
    }

}
