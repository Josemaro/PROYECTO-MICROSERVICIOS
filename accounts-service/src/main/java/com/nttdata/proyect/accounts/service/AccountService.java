package com.nttdata.proyect.accounts.service;

import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountType;

import java.util.List;

public interface AccountService {

    List<Account> findAllAccounts();

    List<Account> findAccountsByType(AccountType type);

    Account createAccount(Account account);

    Account updateAccount(Account account);

    Account deleteAccount(Account account);

    Account getAccount(Long id);

}
