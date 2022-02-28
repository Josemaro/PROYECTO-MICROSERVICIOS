package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.AccountRepository;
import com.nttdata.proyect.accounts.repository.entities.Account;
import com.nttdata.proyect.accounts.repository.entities.AccountType;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    CustomerClient customerClient;

    @Autowired
    AccountService accountService;

    @GetMapping(value = "/getCustomer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id){
        return customerClient.getCustomer(id);
    }

    //get
    @GetMapping
    public ResponseEntity<List<Account>> listAllCustomers(@RequestParam(name = "type", required = false) Long typeId) {
        List<Account> accounts = new ArrayList<>();
        if (null == typeId) {
            accounts = accountService.findAllAccounts();
            if (accounts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        } else {
            AccountType type = new AccountType();
            type.setId(typeId);
            accounts = accountService.findAccountsByType(type);
            if (null == accounts) {
                log.error("Accounts with type id {} not found.", accounts);
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.ok(accounts);
    }
    //put
    //post
    //delete
}
