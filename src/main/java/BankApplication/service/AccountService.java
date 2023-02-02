package BankApplication.service;

import BankApplication.model.Account;

import java.util.List;

public interface AccountService {

    List<Account> getAllAccounts();
    Account findByAccountNumber(Long accountNumber);

}
