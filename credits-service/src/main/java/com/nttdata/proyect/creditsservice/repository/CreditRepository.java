package com.nttdata.proyect.creditsservice.repository;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit,Long> {
    List<Credit> findByCustomerId(Long id);
}
