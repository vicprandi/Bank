package BankApplication.account.service;

import BankApplication.model.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();

    /* Achar conta pelo Id do CLiente */
    Long findAccountNumberByClientId(Long id);
}
