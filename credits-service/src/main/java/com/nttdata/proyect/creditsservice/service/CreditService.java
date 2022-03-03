package com.nttdata.proyect.creditsservice.service;

import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.Payment;

import java.util.List;

public interface CreditService {
    List<Credit> findAllCredits();

    List<Credit> findAllByCustomerId(Long customerId);

    Credit createCredit(Credit credit);

    Payment createPayment(Payment payment);

    Credit updateCredit(Credit credit);

    Payment payAPayment(Payment payment);

//    void deleteCredit(Credit credit);

    Credit getCredit(Long id);

    Payment getPayment(Long id);

    List<Payment> getPaymentsByCreditId(Long id);

    boolean canGetACredit(Customer customer);
}
