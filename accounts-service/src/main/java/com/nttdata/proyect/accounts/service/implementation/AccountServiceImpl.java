package com.nttdata.proyect.accounts.service.implementation;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.*;
import com.nttdata.proyect.accounts.repository.entities.*;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerClient customerClient;

    @Autowired
    AccountOwnerRepository accountOwnerRepository;

    @Autowired
    AccountSignerRepository accountSignerRepository;

    @Autowired
    AccountTypeRepository accountTypeRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    MovementTypeRepository movementTypeRepository;


    @Override
    public List<Account> findAllAccounts() {
        log.info("\n\n===================\nfindAllAccounts() in AccountServiceImpl");
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAccountsByType(AccountType type) {
        log.info("\n\n===================\nfindAccountsByType() in AccountServiceImpl");
        return accountRepository.findByType(type);
    }

    @Override
    public Account createAccount(String accountNumber,Double initialBalance, AccountType accountType, Customer customer) {
        log.info("\n\n===================\ncreateAccount() in AccountServiceImpl");
        List<AccountOwner> owners = new ArrayList<>();
        AccountOwner owner = new AccountOwner();
        owner.setCustomerId(customer.getId());
        owner.setCustomer(customer);
        owners.add(owner);
        int accountTypeId = Integer.parseInt(accountType.getId().toString());
        double commission = 0D;
        int movementsLimit = 0;
        switch (accountTypeId)
        {
            case 1:
                commission = 0D;
                movementsLimit = 15;
                break;
            case 2:
                commission = 10D;
                movementsLimit = Integer.MAX_VALUE;
                break;
            case 3:
                commission = 0D;
                movementsLimit = 1;
                break;
        }
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(initialBalance);
        account.setCommission(commission);
        account.setMovementsLimit(movementsLimit);
        account.prePersist();
        account.setType(accountType);
        Account accountDB = accountRepository.save(account);
        owner.setAccount(accountDB);
        accountOwnerRepository.save(owner);

        return getAccount(accountDB.getId());
    }

    @Override
    public Account updateAccount(Account account) {
        log.info("\n\n===================\nupdateAccount() in AccountServiceImpl");
        Account accountDB = getAccount(account.getId());
        if (accountDB == null) {
        log.info("\n\n===================\naccountDB is null");
            return null;
        }
        accountDB.setState(account.getState());
        accountDB.setBalance(account.getBalance());
        accountDB.setCommission(account.getCommission());
        accountDB.setMovements(account.getMovements());
        accountDB.setMovementsLimit(account.getMovementsLimit());

        return accountRepository.save(accountDB);
    }

//    @Override
//    public Account deleteAccount(Account account) {
//        return null;
//    }

    @Override
    public Account getAccount(Long id) {
        log.info("\n\n===================\ngetAccount() in AccountServiceImpl");
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public AccountType getAccountType(Long id) {
        log.info("\n\n===================\ngetAccountType() in AccountServiceImpl");
        return accountTypeRepository.findById(id).orElse(null);
    }

    @Override
    public Account addOwner(Account account, Customer customer) {
        log.info("\n\n===================\naddOwner() in AccountServiceImpl");
        /*
         * SOLO CUENTAS EMPRESARIALES PUEDEN TENER VARIOS TITULARES
         */
        Long firstOwnerId = account.getOwners().get(0).getCustomerId();
        Long customerOwnerType = customerClient.getCustomer(firstOwnerId).getBody().getCategory().getId();
        //  1L == CATEGORIA PERSONAL EN EL PRIMER PROPIETARIO DE LA CUENTA
        if(customerOwnerType==1L){
            log.info("\n\n===================\nSOLO CUENTAS EMPRESARIALES PUEDEN TENER VARIOS TITULARES");
            return null;
        }
        Long newOwner = customer.getCategory().getId();
        if(newOwner==1L){
            log.info("\n\n===================\nSOLO CUENTAS EMPRESARIALES PUEDEN SER TITULARES DE OTRAS CUENTAS");
            return null;
        }
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
    public Account addSigner(Account account, Customer customer) {
        /*
         * SOLO CUENTAS EMPRESARIALES PUEDEN TENER FIRMANTES AUTORIZADOS
         */
        Long firstOwnerId = account.getOwners().get(0).getCustomerId();
        Long customerOwnerType = customerClient.getCustomer(firstOwnerId).getBody().getCategory().getId();
        //  1L == CATEGORIA PERSONAL EN EL PRIMER PROPIETARIO DE LA CUENTA
        if(customerOwnerType==1L){
            log.info("\n\n===================\nSOLO CUENTAS EMPRESARIALES PUEDEN FIRMANTES");
            return null;
        }
        //  1L == CATEGORIA PERSONAL EN EL PRIMER PROPIETARIO DE LA CUENTA
        Long newSigner = customer.getCategory().getId();
        if(newSigner==1L){
            log.info("\n\n===================\nSOLO CUENTAS EMPRESARIALES PUEDEN SER FIRMANTES DE OTRAS CUENTAS");
            return null;
        }
        log.info("\n\n===================\naddSigner() in AccountServiceImpl");
        List<AccountSigner> signers = new ArrayList<>();
        AccountSigner signer = new AccountSigner();
        signer.setCustomerId(customer.getId());
        signer.setCustomer(customer);
        signers.add(signer);
        account.setSigners(signers);

        Account accountDB = accountRepository.save(account);
        signer.setAccount(accountDB);
        //If accountDB is successfully created
        accountSignerRepository.save(signer);

        return getAccount(accountDB.getId());
    }

    @Override
    public int getOwnedAccountsByCustomerId(Long id) {
        return accountOwnerRepository.findByCustomerId(id).size();
    }

    @Override
    public int getTotalMovementsByAccount(Long id) {
        return movementRepository.findByAccountId(id).size();
    }

    @Override
    public Double getBalanceByAccount(Long id) {
        Account account = getAccount(id);
        if (account == null){
            return null;
        }
        return account.getBalance();
    }

    @Override
    public MovementType getMovementType(Long id) {
        return movementTypeRepository.getById(id);
    }

//    @Override
//    public LocalDate getRegisterDate(Long id) {
//        Account account = getAccount(id);
//        return account.getCreateAt();
//    }

    // -------------------Map Functions----------------------------------------------

    public List<AccountOwner> mapOwners(Account account) {
        return account.getOwners().stream().map(owner -> {
            owner.setCustomer(customerClient.getCustomer(owner.getCustomerId()).getBody());
            return owner;
        }).collect(Collectors.toList());
    }

    public List<AccountSigner> mapSigners(Account account) {
        return account.getSigners().stream().map(signer -> {
            signer.setCustomer(customerClient.getCustomer(signer.getCustomerId()).getBody());
            return signer;
        }).collect(Collectors.toList());
    }

    @Override
    public Movement saveMovement(Account account, MovementType type, Double amount) {
        int typeId = Integer.parseInt(type.getId().toString());
        if(account.getBalance()<amount && typeId == 1){
            return null;
        }

        Movement movement = new Movement();
        movement.setAccount(account);
        movement.setType(type);
        movement.setAmount(amount);
        // prePersist se encarga de colocar la fecha de creacion en el objeto
        movement.prePersist();

        return movementRepository.save(movement);
    }

    public double calcFinalBalance(Double initialBalance, int typeId, Double amount){
        double finalBalance = 0D;
        if (typeId == 1) {
            if (initialBalance >= amount) {
                //--------------------------WITHDRAW = RETIRO-------------------------------------------
                finalBalance = (initialBalance- (amount));
            } else {
                return 0D;
            }
        } else if (typeId == 2) {
            //-------------------------DEPOSIT = DEPOSITO--------------------------------------------
            finalBalance = initialBalance + (amount);
        }
        return finalBalance;
    }

    @Override
    public Integer getTotalMovementsOfTheMonthByAccount(Long id) {
        Account account= getAccount(id);
        if(account==null){
            return null;
        }
        LocalDate cal= LocalDate.now();
        int thisMonth = cal.getMonth().getValue();
        int thisYear = cal.getYear();
        int nextMonth = thisMonth; // BEGINNING
        int nextYear = thisYear;
        if(thisMonth == 12){
            nextMonth = 1;
            nextYear = thisYear+1;
        }else{
            nextMonth=thisMonth+1;
        }
        log.info("\nyear====>{}\nmonth====>{}",thisYear,thisMonth);
        log.info("\nyear====>{}\nnextMonth====>{}",nextYear,nextMonth);

        String auxStartDateString = Integer.toString(thisYear)+"-"+Integer.toString(thisMonth)+"-"+1;
        String auxEndDateString = Integer.toString(nextYear)+"-"+Integer.toString(nextMonth)+"-"+1;
        log.info("\nstartDate====>{}",auxStartDateString);

        int accountId = id.intValue();
        int totalMovementsInThisMonth =  movementRepository.getAllBetweenDates(accountId,auxStartDateString,auxEndDateString).size();
        log.info("\n{}",totalMovementsInThisMonth);
        return totalMovementsInThisMonth;
    }

}
