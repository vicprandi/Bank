package BankApplication.service;

import BankApplication.model.Account;
import BankApplication.model.Client;
import BankApplication.repository.AccountRepository;
import BankApplication.repository.ClientRepository;
import BankApplication.requests.AccountRequest;
import BankApplication.requests.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final ClientRepository clientRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
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
    public String transferMoney(AccountRequest firstAccountRequest, AccountRequest secondAccountRequest, ClientRequest firstClientRequest, ClientRequest secondClientRequest, Long amount) {

        Account firstAccount = accountRepository.getReferenceById(firstAccountRequest.getBalanceMoney());
        Account firstAccountNumber = accountRepository.getReferenceById(firstAccountRequest.getAccountNumber());
        Long firstAccountBalance = firstAccount.getBalanceMoney();
        Optional<Client> firstAccountCPF = clientRepository.findByCPF(firstClientRequest.getCPF());

        Account secondAccount = accountRepository.getReferenceById(secondAccountRequest.getBalanceMoney());
        Account secondAccountNumber = accountRepository.getReferenceById(secondAccountRequest.getAccountNumber());
        Long secondAccountBalance = secondAccount.getBalanceMoney();
        Optional<Client> secondAccountCPF = clientRepository.findByCPF(secondClientRequest.getCPF());

        /* Primeira regra: Precisa existir o account number */
        if (firstAccountNumber.getAccountNumber() < 0 || secondAccountNumber.getAccountNumber() < 0) throw new RuntimeException("Conta inexistente");

        /* Segunda regra: Ter saldo na conta que vai sair o dinheiro*/
        if (firstAccount.getBalanceMoney() < 0) throw new RuntimeException("Saldo insuficiente");

        /*Terceira regra: Constas não podem ser iguais*/
        if (Objects.equals(firstAccountCPF, secondAccountCPF)) throw new RuntimeException("CPFs idênticos");

        /* Agora sim, a transferência entre contas. */
        long diminuirSaldo = firstAccountBalance - amount;
        secondAccountBalance += amount;
        this.accountRepository.save(firstAccount);
        this.accountRepository.save(secondAccount);


        long somarSaldo = firstAccountBalance + amount;
        secondAccountBalance -= amount;
        this.accountRepository.save(firstAccount);
        this.accountRepository.save(secondAccount);

        return "Transferência realizada com sucesso";
    }


    /* Usando o cliente, preciso observar o extrato. No extrato, deve mostrar as trasnferências, depósitos e saques. */
}
