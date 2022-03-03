package com.nttdata.proyect.creditsservice.service;

import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.repository.entities.Consumption;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;

import java.util.List;

public interface CreditCardService {

    CreditCard findCreditCardById(Long id);

    List<CreditCard> findAllCreditCards();

    List<CreditCard> findAllByCustomerId( Long customerId );

    List<Consumption> findAllConsumption(Long creditCardId);

    CreditCard registerCreditCard(CreditCard credit);

    boolean canGetACreditCard(Customer customer);

    Consumption saveConsumption(CreditCard creditCard,Consumption consumption);
}
