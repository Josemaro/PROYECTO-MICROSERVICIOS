package com.nttdata.proyect.accounts.service.implementation;

import com.nttdata.proyect.accounts.client.CustomerClient;
import com.nttdata.proyect.accounts.models.Customer;
import com.nttdata.proyect.accounts.repository.*;
import com.nttdata.proyect.accounts.repository.entities.*;
import com.nttdata.proyect.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findAccountsByType(AccountType type) {
        return accountRepository.findByType(type);
    }

    @Override
    public Account createAccount(Account account, AccountType accountType, Customer customer) {

        List<AccountOwner> owners = new ArrayList<>();
        AccountOwner owner = new AccountOwner();
        owner.setCustomerId(customer.getId());
        owner.setCustomer(customer);
        owners.add(owner);
        account.setOwners(owners);
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
        Movement movement = new Movement();
        movement.setAccount(account);
        movement.setType(type);
        movement.setAmount(amount);

        Double balance = account.getBalance();
        Double commision = account.getCommission();

        //1 retiro 2 depostio
        if (type.getId() == 1) {
            //BALANCE O TOTAL TIENE QUE SER MAYOR A LA SUMA DE MI RETIRO
            if (balance >= amount + commision) {
                account.setBalance(account.getBalance() - (amount + commision));
            } else {
                return null;
            }
        } else if (type.getId() == 2) {
            //COMISION - BALANCE <= MONTO A DEPOSITAR
            if (amount >= (commision - balance)) {
                account.setBalance(account.getBalance() + (amount - commision));
            } else {
                return null;
            }
        }
        updateAccount(account);
        return movementRepository.save(movement);
    }

}
