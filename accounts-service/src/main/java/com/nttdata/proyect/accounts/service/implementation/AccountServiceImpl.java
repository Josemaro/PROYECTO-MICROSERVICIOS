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
import java.util.Calendar;
import java.util.Date;
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
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAccountsByType(AccountType type) {
        return accountRepository.findByType(type);
    }

    @Override
    public Account createAccount(String accountNumber,Double initialBalance, AccountType accountType, Customer customer) {
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
                movementsLimit = 30;
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
        Account accountDB = getAccount(account.getId());
        if (accountDB == null) {
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
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public AccountType getAccountType(Long id) {
        return accountTypeRepository.findById(id).orElse(null);
    }

    @Override
    public Account addOwner(Account account, Customer customer) {

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
        Date date= new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
        int thisMonth = cal.getMonth().getValue();
        int thisYear = cal.getYear();
        int thisDay = cal.getDayOfMonth();

//        int thisMonth = cal.get(Calendar.MONTH);
//        int thisYear = cal.get(Calendar.YEAR);
//        int thisDay = cal.get(Calendar.DATE);
//        int thisHour = cal.get(Calendar.HOUR);
//        int nextMonth = thisMonth; // BEGINNING
//        int nextYear = thisYear;
//        if(thisMonth == 12){
//            nextMonth = 1;
//            nextYear = thisYear+1;
//        }
        Calendar auxStartDate = Calendar.getInstance();
        Calendar auxEndDate = Calendar.getInstance();

        auxStartDate.set(thisYear,thisMonth,1);
        auxEndDate.set(thisYear,thisMonth,1);

        log.info("\nyear====>{}\nmonth====>{}\nday====>{}",thisYear,thisMonth,thisDay);
        Date startDate = auxStartDate.getTime();
        Date endDate = auxEndDate.getTime();

        return movementRepository.getAllBetweenDates(startDate,endDate).size();
    }

}
