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
        return creditRepository.findAll();
    }

    @Override
    public Credit createCredit(Credit credit) {
        return creditRepository.save(credit);
    }

    @Override
    public Credit updateCredit(Credit credit) {
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
        return creditRepository.findById(id).orElse(null);
    }
}
