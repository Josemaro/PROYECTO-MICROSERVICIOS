package com.nttdata.proyect.customerservice.controller;

import com.nttdata.proyect.customerservice.repository.entities.Customer;
import com.nttdata.proyect.customerservice.repository.entities.CustomerCategory;
import com.nttdata.proyect.customerservice.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> listAllCustomers(@RequestParam(name = "category" , required = false) Long categoryId ) {
        List<Customer> customers =  new ArrayList<>();
        if (null ==  categoryId) {
            customers = customerService.findAllCustomers();
            if (customers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        }else{
            CustomerCategory category= new CustomerCategory();
            category.setId(categoryId);
            customers = customerService.findCustomersByCategory(category);
            if ( null == customers ) {
                log.error("Customers with Region id {} not found.", category);
                return  ResponseEntity.notFound().build();
            }
        }

        return  ResponseEntity.ok(customers);
    }

}
