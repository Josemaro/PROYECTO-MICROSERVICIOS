package com.nttdata.proyect.creditsservice.repository;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit,Long> {
}
