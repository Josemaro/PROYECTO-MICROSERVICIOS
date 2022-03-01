package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.models.RegistrationRequestBody;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import com.nttdata.proyect.accounts.repository.entities.AccountSigner;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    CustomerClient customerClient;

    @Autowired
    AccountService accountService;

    @GetMapping(value = "/getCustomer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id) {
        return customerClient.getCustomer(id);
    }

    @GetMapping(value = "/getCustomer/totalAccounts/{id}")
    public Integer getTotalOfCustomerAccounts(@PathVariable("id") Long id) {
        return accountService.getOwnedAccountsByCustomerId(id);
    }

    // -------------------Retrieve all the accounts-------------------------------------------

    @GetMapping
    public ResponseEntity<List<Account>> listAllAccounts(@RequestParam(name = "type", required = false) Long typeId) {
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.findAllAccounts();

        List<Account> accountsFinal = accounts.stream().map(account -> {
            List<AccountOwner> ownerList = mapOwners(account);
            account.setOwners(ownerList);
            return account;
        }).collect(Collectors.toList());

        accountsFinal = accountsFinal.stream().map(account -> {
            List<AccountSigner> signerList = mapSigners(account);
            account.setSigners(signerList);
            return account;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(accountsFinal);
    }

    // -------------------Retrieve an Account-------------------------------------------

    @GetMapping(value = "/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {
        log.info("Fetching Account with id {}", id);
        Account account = accountService.getAccount(id);
        if (null == account) {
            log.error("Account with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }

        account.setOwners(mapOwners(account));
        account.setSigners(mapSigners(account));

        account.getOwners().stream().map(accountOwner -> {
            log.info("{}", accountOwner.getCustomerId());
            return accountOwner.getCustomerId();
        }).collect(Collectors.toList());
        return ResponseEntity.ok(account);
    }


    // -------------------Create a Account-------------------------------------------

    @PostMapping(value = "/register")
    public ResponseEntity<Account> createAccount(@RequestBody RegistrationRequestBody registrationRequestBody) {
        log.info("Creating Account....");
        String customerDni = registrationRequestBody.getCustomerDni();
        Customer customerDB = customerClient.getCustomerByDni(customerDni).getBody();
        if (customerDB == null) {
            log.info("Customer Not Found");
            return ResponseEntity.notFound().build();
        }

        AccountType accountType = accountService.getAccountType(registrationRequestBody.getAccountTypeId());
        if (accountType == null) {
            log.info("AccountType Not Found");
            return ResponseEntity.notFound().build();
        }
        // A business customer can't have a fix-term account or savings account
        if (customerDB.getCategory().getId() == 2 && (accountType.getId() == 1 || accountType.getId() == 3)) {
            log.info("A business customer can't have a fix-term account or savings account");
            return ResponseEntity.badRequest().build();
        }
        // A personal customer only can have one account
        int numberOfAccounts = accountService.getOwnedAccountsByCustomerId(customerDB.getId());
        if (customerDB.getCategory().getId() == 1 && numberOfAccounts >= 1) {
            log.info("A personal customer only can have one account ");
            return ResponseEntity.badRequest().build();
        }

        Account newAccount = registrationRequestBody.getAccount();
        Account accountDB = accountService.createAccount(newAccount, accountType, customerDB);
        return ResponseEntity.ok(accountDB);
    }

    // -------------------Map Functions----------------------------------------------

    public List<AccountOwner> mapOwners(Account account) {
        return account.getOwners().stream().map(owner -> {
            owner.setCustomer(getCustomer(owner.getCustomerId()).getBody());
            return owner;
        }).collect(Collectors.toList());
    }

    public List<AccountSigner> mapSigners(Account account) {
        return account.getSigners().stream().map(signer -> {
            signer.setCustomer(getCustomer(signer.getCustomerId()).getBody());
            return signer;
        }).collect(Collectors.toList());
    }
}