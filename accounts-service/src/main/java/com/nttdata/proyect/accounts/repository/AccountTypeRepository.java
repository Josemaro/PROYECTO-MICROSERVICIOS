package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends JpaRepository<AccountType,Long> {
}
