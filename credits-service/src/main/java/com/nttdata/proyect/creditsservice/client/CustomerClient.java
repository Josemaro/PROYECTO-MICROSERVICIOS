package com.nttdata.proyect.creditsservice.client;

import com.nttdata.proyect.creditsservice.models.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service",path = "/customers")
public interface CustomerClient {

    @GetMapping(value = "/{id}")
    ResponseEntity<Customer> getCustomerById(@PathVariable("id") Long id);

    @GetMapping(value = "/dni/{dni}")
    ResponseEntity<Customer> getCustomerByDni(@PathVariable("dni") String dni);

}
