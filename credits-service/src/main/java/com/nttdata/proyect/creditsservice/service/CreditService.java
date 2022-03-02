package com.nttdata.proyect.creditsservice.service;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.Payment;

import java.util.List;

public interface CreditService {
    List<Credit> findAllCredits();

    List<Credit> findAllByCustomerId(Long customerId);

    Credit createCredit(Credit credit);

    Payment createPayment(Payment payment);

    Credit updateCredit(Credit credit);

    void deleteCredit(Credit credit);

    Credit getCredit(Long id);

    List<Payment> getPaymentsByCreditId(Long id);

}
