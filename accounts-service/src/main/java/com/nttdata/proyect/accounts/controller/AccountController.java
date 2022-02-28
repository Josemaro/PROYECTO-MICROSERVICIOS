package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.AccountRepository;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import com.nttdata.proyect.accounts.repository.entities.AccountSigner;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
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

    //get
    @GetMapping
    public ResponseEntity<List<Account>> listAllAccounts(@RequestParam(name = "type", required = false) Long typeId) {
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.findAllAccounts();

        List<Account> accountsFinal =  accounts.stream().map(account -> {
            List<AccountOwner> ownerList = account.getOwners().stream().map(owner -> {
                owner.setCustomer(getCustomer(owner.getCustomerId()).getBody());
                return owner;
            }).collect(Collectors.toList());
            account.setOwners(ownerList);
            return account;
        }).collect(Collectors.toList());

        accountsFinal =  accountsFinal.stream().map(account -> {
            List<AccountSigner> signerList = account.getSigners().stream().map(signer -> {
                signer.setCustomer(getCustomer(signer.getCustomerId()).getBody());
                return signer;
            }).collect(Collectors.toList());
            account.setSigners(signerList);
            return account;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(accountsFinal);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {
        log.info("Fetching Customer with id {}", id);
        Account account = accountService.getAccount(id);
        if (null == account) {
            log.error("Customer with id {} not found.", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
    //put
    //post
    //delete


    //mapping Customers
//    public List<Customer> mapCustomers (List<Entity> customers){
//
//        return
//    };
}