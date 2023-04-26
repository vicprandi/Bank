package bank.account.service;

import bank.model.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();

    /* Achar conta pelo Id do CLiente */
    Long findAccountNumberByCustomerId(Long id);
}