package com.nttdata.proyect.customerservice.repository;

import com.nttdata.proyect.customerservice.repository.entities.Customer;
import com.nttdata.proyect.customerservice.repository.entities.CustomerCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository <Customer,Long> {
    Customer findByNumberID(String numberID);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByCategory(CustomerCategory category);
}
