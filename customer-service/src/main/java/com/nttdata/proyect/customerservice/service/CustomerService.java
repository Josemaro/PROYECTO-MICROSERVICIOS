package com.nttdata.proyect.customerservice.service;

import com.nttdata.proyect.customerservice.repository.entities.Customer;
import com.nttdata.proyect.customerservice.repository.entities.CustomerCategory;

import java.util.List;

public interface CustomerService {

    List<Customer> findAllCustomers();

    List<Customer> findCustomersByCategory(CustomerCategory category);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    Customer deleteCustomer(Customer customer);

    Customer getCustomer(Long id);

    Customer getCustomerByNumberID(String numberID);

}
