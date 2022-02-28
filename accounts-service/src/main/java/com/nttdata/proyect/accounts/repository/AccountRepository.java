package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
