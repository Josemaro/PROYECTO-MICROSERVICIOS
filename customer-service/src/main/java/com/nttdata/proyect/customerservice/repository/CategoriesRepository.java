package com.nttdata.proyect.customerservice.repository;

import com.nttdata.proyect.customerservice.repository.entities.CustomerCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<CustomerCategory,Long> {
}
