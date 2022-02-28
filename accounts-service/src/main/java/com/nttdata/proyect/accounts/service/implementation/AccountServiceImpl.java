package com.nttdata.proyect.accounts.service.implementation;

import com.nttdata.proyect.accounts.repository.AccountOwnerRepository;
import com.nttdata.proyect.accounts.repository.AccountRepository;
import com.nttdata.proyect.accounts.repository.AccountSignerRepository;
import com.nttdata.proyect.accounts.repository.MovementRepository;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountOwnerRepository accountOwnerRepository;

    @Autowired
    AccountSignerRepository accountSignerRepository;

    @Autowired
    MovementRepository movementRepository;


    @Override
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAccountsByType(AccountType type) {
        return accountRepository.findByType(type);
    }

    @Override
    public Account createAccount(Account account) {

        Account accountDB = accountRepository.getById(account.getId());

        if (accountDB.getId() == null) {
            return accountDB;
        }

        account.setState("CREATED");
        accountDB = accountRepository.save(account);
        return accountDB;

    }

    @Override
    public Account updateAccount(Account customer) {
        return null;
    }

    @Override
    public Account deleteAccount(Account customer) {
        return null;
    }

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

}
