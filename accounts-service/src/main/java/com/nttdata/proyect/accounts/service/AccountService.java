package com.nttdata.proyect.accounts.service;

import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountType;

import java.util.List;

public interface AccountService {

    List<Account> findAllAccounts();

    List<Account> findAccountsByType(AccountType type);

    Account createAccount(Account customer);

    Account updateAccount(Account customer);

    Account deleteAccount(Account customer);

    Account getAccount(Long id);

}
