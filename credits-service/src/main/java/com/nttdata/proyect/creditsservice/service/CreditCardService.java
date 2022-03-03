package com.nttdata.proyect.creditsservice.service;

import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;

import java.util.List;

public interface CreditCardService {
    List<CreditCard> findAllCreditCards();

    List<CreditCard> findAllByCustomerId( Long customerId );

    CreditCard createCreditCard(CreditCard credit);

}
