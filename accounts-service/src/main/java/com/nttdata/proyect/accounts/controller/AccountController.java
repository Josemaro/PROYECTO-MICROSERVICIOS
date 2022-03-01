package com.nttdata.proyect.accounts.controller;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.exception.AccountException;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.models.requestBody.AddOwnerSignerBody;
import com.nttdata.proyect.accounts.models.requestBody.MovementRequestBody;
import com.nttdata.proyect.accounts.models.requestBody.RegistrationRequestBody;
import com.nttdata.proyect.accounts.repository.entities.*;
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

    // -------------------Retrieve Customer by Id or Dni-------------------------------------------


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

        log.info("mapping owners stream function");
        List<Account> accountsFinal = accounts.stream().map(account -> {
            List<AccountOwner> ownerList = accountService.mapOwners(account);
            account.setOwners(ownerList);
            return account;
        }).collect(Collectors.toList());

        log.info("mapping owners mapping signers");
        accountsFinal = accountsFinal.stream().map(account -> {
            List<AccountSigner> signerList = accountService.mapSigners(account);
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

        account.setOwners(accountService.mapOwners(account));
        account.setSigners(accountService.mapSigners(account));

        return ResponseEntity.ok(account);
    }

    // -------------------Create a Account-------------------------------------------

    @PostMapping(value = "/register")
    public ResponseEntity<Account> createAccount(@RequestBody RegistrationRequestBody registrationRequestBody) {
        log.info("Creating Account....");
        String customerDni = registrationRequestBody.getCustomerDni();
        Customer customerDB = customerClient.getCustomerByDni(customerDni).getBody();


        if (customerDB == null) {
            log.info("CUSTOMER NOT FOUND!!!!!!!");
            return ResponseEntity.notFound().build();
        }

        AccountType accountType = accountService.getAccountType(registrationRequestBody.getAccountTypeId());
        if (accountType == null) {
            log.info("ACCOUNT TYPE NOT FOUND!!!!!!!");
            return ResponseEntity.notFound().build();
        }
        //---------------------- A business customer can't have a fix-term account or savings account-------------------
        /*Un cliente empresarial no puede tener una cuenta de ahorro o de plazo fijo, pero sí
         *múltiples cuentas corrientes.
         */
        if (customerDB.getCategory().getId() == 2 && (accountType.getId() == 1 || accountType.getId() == 3)) {
            log.info("A BUSINESS CUSTOMER CAN'T HAVE A FIX-TERM ACCOUNT OR SAVINGS ACCOUNT");
            return ResponseEntity.badRequest().build();
        }
        // ----------------------A personal customer only can have one account------------------------------------------
        /*
         *  Un cliente personal solo puede tener un máximo de una cuenta de ahorro, una cuenta
         *  corriente o cuentas a plazo fijo
         */
        int numberOfAccounts = accountService.getOwnedAccountsByCustomerId(customerDB.getId());
        if (customerDB.getCategory().getId() == 1 && numberOfAccounts >= 1) {
            log.info("A PERSONAL CUSTOMER ONLY CAN HAVE ONE ACCOUNT ");
            return ResponseEntity.badRequest().build();
        }

        Double initialBalance = registrationRequestBody.getBalance();
        String accountNumber = registrationRequestBody.getAccountNumber();

        Account accountDB = accountService.createAccount(accountNumber,initialBalance, accountType, customerDB);
        return ResponseEntity.ok(accountDB);
    }

    // -------------------Add Owners and Signers------------------------------------------------------------------------
    /*
     *  Las cuentas bancarias empresariales pueden tener uno o más titulares y cero o más
     *  firmantes autorizados.
     */
    @PostMapping(value = "/addOwner")
    public ResponseEntity<Account> addOwner(@RequestBody AddOwnerSignerBody addOwnerSignerBody){
        log.info("IN ADD OWNER FUNCTION");
        Customer customer = customerClient.getCustomerByDni(addOwnerSignerBody.getCustomerDni()).getBody();
        Account account = getAccount(addOwnerSignerBody.getAccountId()).getBody();
        Account accountDB = accountService.addOwner(account,customer);
        return ResponseEntity.ok(accountDB);
    }

    @PostMapping(value = "/addSigner")
    public ResponseEntity<Account> addSigner(@RequestBody AddOwnerSignerBody addOwnerSignerBody){
        log.info("IN ADD SIGNER FUNCTION");
        Customer customer = customerClient.getCustomerByDni(addOwnerSignerBody.getCustomerDni()).getBody();
        Account account = getAccount(addOwnerSignerBody.getAccountId()).getBody();
        Account accountDB = accountService.addSigner(account,customer);
        return ResponseEntity.ok(accountDB);
    }

    // -------------------MOVEMENTS------------------------------------------

    @PostMapping(value = "/movement/{accountId}")
    public ResponseEntity<Movement> makeMovement(@RequestParam(value = "accountId") Long accountId, @RequestBody MovementRequestBody movementRequestBody) throws AccountException {
        log.info("MAKING A MOVEMENT");
        Account account = getAccount(accountId).getBody();
        MovementType type = accountService.getMovementType(movementRequestBody.getTypeId());
        Double amount = movementRequestBody.getAmount();
        Movement movement = accountService.saveMovement(account,type,amount);

        if(movement==null){
//          throw new AccountException("xd");
            log.info("INVALID MOVEMENT REQUEST");
            return  ResponseEntity.badRequest().build();
        };

        int typeId = Integer.parseInt(type.getId().toString());
        double balance = account.getBalance();
        Double finalBalance = accountService.calcFinalBalance(balance,typeId, movement.getAmount());
        account.setBalance(finalBalance);
        Account accountDB =  accountService.updateAccount(account);
        if(accountDB == null){
            log.info("CAN'T UPDATE ACCOUNT");
            return  ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(movement);
    }

    @GetMapping(value = "/{id}/balance")
    public ResponseEntity<Double> balanceByAccountId(@RequestParam("id") Long id){
        Double balance = accountService.getBalanceByAccount(id);
        return ResponseEntity.ok().body(balance);
    }



}