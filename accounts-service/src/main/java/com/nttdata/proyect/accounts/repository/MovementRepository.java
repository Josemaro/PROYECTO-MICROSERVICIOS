package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MovementRepository extends JpaRepository<Movement,Long> {
    List<Movement> findByAccountId(Long id);
}
