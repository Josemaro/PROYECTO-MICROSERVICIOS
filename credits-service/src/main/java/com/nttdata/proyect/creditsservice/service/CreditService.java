package com.nttdata.proyect.creditsservice.service;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;

import java.util.List;

public interface CreditService {
    List<Credit> findAllCredits();

    List<Credit> findAllByCustomerId(Long customerId);

    Credit createCredit(Credit credit);

    Credit updateCredit(Credit credit);

    void deleteCredit(Credit credit);

    Credit getCredit(Long id);
}
