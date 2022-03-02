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

import java.time.LocalDate;
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

    @GetMapping(value = "{id}/movements")
    public Integer getTotalMovementsOfTheMonth(@PathVariable("id") Long id) {
        return accountService.getTotalMovementsOfTheMonthByAccount(id);
    }
    // -------------------Retrieve all the accounts-------------------------------------------

    @GetMapping
    public ResponseEntity<List<Account>> listAllAccounts(@RequestParam(name = "type", required = false) Long typeId) {
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.findAllAccounts();

        /*
         *  Al momento de traer el arreglo de cuentas
         *  mapeo a los titulares y firmantes autorizados
         *  ya que en su lista traen su id de cliente
         *  dicha entidad se encuentra en otro endpoint
         *
         */
        log.info("mapping owners and signers in stream function");
        List<Account> accountsFinal = accounts.stream().map(account -> {
            List<AccountOwner> ownerList = accountService.mapOwners(account);
            account.setOwners(ownerList);
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
    // ------------------------------MAKE A MOVEMENT-------------------------

    @PostMapping(value = "/movement/{accountId}")
    public ResponseEntity<Movement> makeMovement(@RequestParam(value = "accountId") Long accountId, @RequestBody MovementRequestBody movementRequestBody){
        log.info("MAKING A MOVEMENT");
        /*
         * Obtengo la cuenta de la base de datos
         * Obtengo el monto y el tipo de movimiento para ser usados posteriormente al momento de realizar el movimiento
         */
        MovementType type = accountService.getMovementType(movementRequestBody.getTypeId());
        Double amount = movementRequestBody.getAmount();
        Account account = getAccount(accountId).getBody();
        if(account == null){
            log.info("\n\nCUENTA NO ENCONTRADA\n\n");
            return ResponseEntity.notFound().build();
        }
        /*
         * OBTENGO LOS MOVIMIENTOS DE ESTE MES DE LA CUENTA Y VERIFICO QUE NO SOBREPASE EL LIMITE
         */
        int totalMovementsInThisMonth = getTotalMovementsOfTheMonth(account.getId());
        if(totalMovementsInThisMonth+1>account.getMovementsLimit()){
            log.info("\n\nNO SE PUEDEN REALIZAR MAS MOVIMIENTOS\n\n");
            return ResponseEntity.badRequest().build();
        }
        /*
         *  CASO: SI LA CUENTA ES DE PLAZO FIJO SOLO PUEDE HACER UNA OPERACION AL MES UN DÍA ESPECIFICO
         *  (NUMERO DE LA FECHA DE REGISTRO)
         */
        if(account.getType().getId()==3){
            LocalDate createAt =  account.getCreateAt();
            LocalDate today = LocalDate.now();
            int dayOfMonthNumberToday = today.getDayOfMonth();
            int dayOfCreation = createAt.getDayOfMonth();
            log.info("\nTODAY={}, CREATION DAY={}",dayOfMonthNumberToday,dayOfCreation);
            if(dayOfCreation != dayOfMonthNumberToday){
                log.info("\n\n NO SE PUEDE REALIZAR MOVIMINENTO ESTE DIA EN LA CUENTA DE PLAZO FIJO\n\n");
                return ResponseEntity.badRequest().build();
            }
        }
        /*
         * GUARDAR EL MOVIMIENTO Y VERIFICO
         */
        Movement movement = accountService.saveMovement(account,type,amount);
        if(movement==null){
//          throw new AccountException("xd");
            log.info("INVALID MOVEMENT REQUEST");
            return  ResponseEntity.badRequest().build();
        };
        /*
         * CALCULO EL BALANCE FINAL DE LA CUENTA
         * Y ACTUALIZO EL BALANCE DEPENDIENDO EL TIPO
         * DE MOVIMIENTO
         */
        int typeId = Integer.parseInt(type.getId().toString());
        double balance = account.getBalance();

        Double finalBalance = accountService.calcFinalBalance(balance,typeId, movement.getAmount());
        account.setBalance(finalBalance);
        Account accountDB =  accountService.updateAccount(account);
        /*
         * VERIFICO SI SE ACTUALIZA
         */
        if(accountDB == null){
            log.info("CAN'T UPDATE ACCOUNT");
            return  ResponseEntity.badRequest().build();
        }
        /*
         * Retorno el movimiento
         */
        return ResponseEntity.ok().body(movement);
    }

    // -------------------GET THE BALANCE BY ACCOUNT ------------------------------------------
    // -------------------------CONSULTAR EL SALDO---------------------------------------------

    @GetMapping(value = "/{id}/balance")
    public ResponseEntity<Double> balanceByAccountId(@RequestParam("id") Long id){
        Double balance = accountService.getBalanceByAccount(id);
        return ResponseEntity.ok().body(balance);
    }



}