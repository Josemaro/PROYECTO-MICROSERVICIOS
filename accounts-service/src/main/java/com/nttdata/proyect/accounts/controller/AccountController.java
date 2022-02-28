package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.models.RegistrationRequestBody;
import com.nttdata.proyect.accounts.repository.AccountRepository;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import com.nttdata.proyect.accounts.repository.entities.AccountSigner;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    //get
    @GetMapping
    public ResponseEntity<List<Account>> listAllAccounts(@RequestParam(name = "type", required = false) Long typeId) {
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.findAllAccounts();

        List<Account> accountsFinal =  accounts.stream().map(account -> {
            List<AccountOwner> ownerList = mapOwners(account);
            account.setOwners(ownerList);
            return account;
        }).collect(Collectors.toList());

        accountsFinal =  accountsFinal.stream().map(account -> {
            List<AccountSigner> signerList = mapSigners(account);
            account.setSigners(signerList);
            return account;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(accountsFinal);
    }

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
            log.info("{}",accountOwner.getCustomerId());
            return accountOwner.getCustomerId();

        }).collect(Collectors.toList());
        return ResponseEntity.ok(account);
    }
    //put
    //post
    // -------------------Create a Account-------------------------------------------

    @PostMapping(value = "/register")
    public ResponseEntity<Account> createAccount(@RequestBody RegistrationRequestBody registrationRequestBody) {
        log.info("Creating Account....");
        String customerDni = registrationRequestBody.getCustomerDni();
        Customer customerDB = customerClient.getCustomerByDni(customerDni).getBody();
        if(customerDB==null){
            log.info("CustomerNotFound By Dni");
            return ResponseEntity.notFound().build();
        };
        Account newAccount = registrationRequestBody.getAccount();
        Account accountDB = accountService.createAccount(newAccount,customerDB);
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