package com.nttdata.proyect.creditsservice.repository;

import com.nttdata.proyect.creditsservice.repository.entities.Consumption;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption,Long> {
    List<Consumption> findByCreditCardId(Long creditCardId);
}
