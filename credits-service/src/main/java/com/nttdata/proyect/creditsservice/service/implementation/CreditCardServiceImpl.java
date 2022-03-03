package com.nttdata.proyect.creditsservice.service.implementation;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import com.nttdata.proyect.creditsservice.repository.CreditCardRepository;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;
import com.nttdata.proyect.creditsservice.service.CreditCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    CustomerClient customerClient;

    @Override
    public List<CreditCard> findAllCreditCards() {
        log.info("\n\n===================\nfindAllCreditCards() in creditServiceImpl");
        return creditCardRepository.findAll();
    }

    @Override
    public List<CreditCard> findAllByCustomerId( Long customerId ) {
        log.info("\n\n===================\nfindAllByCustomerId() in creditServiceImpl");
        return creditCardRepository.findByCustomerId(customerId);
    }

    @Override
    public CreditCard createCreditCard(CreditCard credit) {
        log.info("\n\n===================\ncreateCreditCard() in creditServiceImpl");
        return creditCardRepository.save(credit);
    }
}
