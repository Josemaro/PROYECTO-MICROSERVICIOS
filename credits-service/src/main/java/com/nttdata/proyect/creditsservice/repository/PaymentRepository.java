package com.nttdata.proyect.creditsservice.repository;

import com.nttdata.proyect.creditsservice.repository.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByCreditId(Long id);
}
