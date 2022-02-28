package com.nttdata.proyect.accounts.repository;

import com.nttdata.proyect.accounts.repository.entities.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOwnerRepository  extends JpaRepository<AccountOwner,Long> {
    List<AccountOwner> findByCustomerId(Long id);
}
