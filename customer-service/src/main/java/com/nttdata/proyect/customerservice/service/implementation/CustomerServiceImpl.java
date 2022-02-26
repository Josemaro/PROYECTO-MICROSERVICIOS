package com.nttdata.proyect.customerservice.service.implementation;

import com.nttdata.proyect.customerservice.repository.CustomerRepository;
import com.nttdata.proyect.customerservice.repository.entities.Customer;
import com.nttdata.proyect.customerservice.repository.entities.CustomerCategory;
import com.nttdata.proyect.customerservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findCustomersByCategory(CustomerCategory category) {
        return customerRepository.findByCategory(category);
    }

    @Override
    public Customer createCustomer(Customer customer) {

        Customer customerDB = customerRepository.findByNumberID(customer.getNumberID());
        if (customerDB != null) {
            return customerDB;
        }

        customer.setState("CREATED");
        customerDB = customerRepository.save(customer);
        return customerDB;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        Customer customerDB = getCustomer(customer.getId());
        if (customerDB == null){
            return  null;
        }
        customerDB.setFirstName(customer.getFirstName());
        customerDB.setLastName(customer.getLastName());
        customerDB.setEmail(customer.getEmail());

        return  customerRepository.save(customerDB);
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        Customer customerDB = getCustomer(customer.getId());
        if (customerDB ==null){
            return  null;
        }
        customer.setState("DELETED");
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomer(Long id) {
        return  customerRepository.findById(id).orElse(null);
    }
}
