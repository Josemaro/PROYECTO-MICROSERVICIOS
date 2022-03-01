package com.nttdata.proyect.accounts.service;

import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.entities.*;

import java.util.List;

public interface AccountService {

    List<Account> findAllAccounts();

    List<Account> findAccountsByType(AccountType type);

    Account createAccount(Account account,AccountType accountType, Customer customer );

    Account updateAccount(Account account);

//    Account deleteAccount(Account account);

    Account getAccount(Long id);

    AccountType getAccountType(Long id);

    Account addOwner(Account account, Customer customer);

    Account addSigner(Account account, Customer customer);


    //---------------------------------------------------------------------------------

    int getOwnedAccountsByCustomerId(Long id);

    int getTotalMovementsByAccount(Long id);

    Double getBalanceByAccount(Long id);

    MovementType getMovementType(Long id);

    //----------------------MAP FUNCTIONS----------------------------------------------

    List<AccountOwner> mapOwners(Account account);

    List<AccountSigner> mapSigners(Account account);

    Movement saveMovement(Account account, MovementType type, Double amount);


    //----------------------BUSINESS VALIDATION---------------------------------------------

}
