package com.nttdata.proyect.accounts.service.implementation;

import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.*;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    AccountTypeRepository accountTypeRepository;


    @Autowired
    MovementRepository movementRepository;


    @Override
    public List<Account> findAllAccounts() {
        return  accountRepository.findAll();
    }

    @Override
    public List<Account> findAccountsByType(AccountType type) {
        return accountRepository.findByType(type);
    }

    @Override
    public Account createAccount(Account account, Customer customer) {


        List<AccountOwner> owners = new ArrayList<>();
        AccountOwner owner = new AccountOwner();
        owner.setCustomerId(customer.getId());
        owner.setCustomer(customer);
        owners.add(owner);
        account.setOwners(owners);


        Account accountDB = accountRepository.save(account);
        owner.setAccount(accountDB);
        //If accountDB is successfully created
        accountOwnerRepository.save(owner);


        return getAccount(accountDB.getId());

    }

    @Override
    public Account updateAccount(Account account) {
        return null;
    }

    @Override
    public Account deleteAccount(Account account) {
        return null;
    }

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

}
