package com.nttdata.proyect.creditsservice.repository;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard,Long> {
    List<CreditCard> findByCustomerId(Long customerId);
}
