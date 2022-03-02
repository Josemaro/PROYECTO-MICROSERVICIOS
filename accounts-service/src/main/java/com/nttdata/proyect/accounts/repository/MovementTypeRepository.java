package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementTypeRepository extends JpaRepository<MovementType,Long> {
}
