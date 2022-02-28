package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<AccountOwner,Long> {
}
