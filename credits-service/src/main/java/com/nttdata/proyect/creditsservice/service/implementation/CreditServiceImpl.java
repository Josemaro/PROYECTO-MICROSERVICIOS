package com.nttdata.proyect.creditsservice.service.implementation;

import com.nttdata.proyect.creditsservice.repository.CreditRepository;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CreditServiceImpl implements CreditService {
    @Autowired
    CreditRepository creditRepository;

    @Override
    public List<Credit> findAllCredits() {
        log.info("\n\n===================\nfindAllCredits() in creditServiceImpl");
        return creditRepository.findAll();
    }

    @Override
    public List<Credit> findAllByCustomerId( Long customerId ) {
        log.info("\n\n===================\nfindByCustomerId() in creditServiceImpl");
        return creditRepository.findByCustomerId(customerId);
    }

    @Override
    public Credit createCredit(Credit credit) {
        log.info("\n\n===================\ncreateCredit() in creditServiceImpl");
        return creditRepository.save(credit);
    }

    @Override
    public Credit updateCredit(Credit credit) {
        log.info("\n\n===================\nupdateCredit() in creditServiceImpl");
        Credit creditDB = getCredit(credit.getId());
        if (creditDB == null){
            return  null;
        }
//        customerDB.setFirstName(customer.getFirstName());
//        customerDB.setLastName(customer.getLastName());
//        customerDB.setEmail(customer.getEmail());
        return  creditRepository.save(creditDB);
    }

    @Override
    public void deleteCredit(Credit credit) {

    }

    @Override
    public Credit getCredit(Long id) {
        log.info("\n\n===================\ngetCredit() in creditServiceImpl");
        return creditRepository.findById(id).orElse(null);
    }
}
