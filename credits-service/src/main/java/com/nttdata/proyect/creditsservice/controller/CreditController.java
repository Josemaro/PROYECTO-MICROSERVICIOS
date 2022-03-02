package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.models.RBCreditRegistration;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCategory;
import com.nttdata.proyect.creditsservice.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/credits")
public class CreditController {
    @Autowired
    CreditService creditService;
    @Autowired
    CustomerClient customerClient;

    @GetMapping("/getCustomerBy/{id}")
    public ResponseEntity<Customer> getCustomerById (@PathVariable("id") Long id){
        Customer customer = customerClient.getCustomerById(id).getBody();
        return ResponseEntity.ok().body(customer);
    }

    @GetMapping
    public ResponseEntity<List<Credit>> getAllCredits (){
        List<Credit>credits = creditService.findAllCredits();
        List<Credit>creditsWithCustomerData = credits.stream().map(credit -> {
            credit.setCustomer(getCustomerById(credit.getCustomerId()).getBody());
            return credit;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(creditsWithCustomerData);
    }

    @GetMapping("/customerCredits/{id}")
    public ResponseEntity<List<Credit>> getAllCreditsOfOneCustomer (@PathVariable("id") Long id){

        Customer customer = getCustomerById(id).getBody();
        if(customer==null){
//            log.info("\nCliente no encontrado");
            return ResponseEntity.notFound().build();
        }
        List<Credit>credits = creditService.findAllByCustomerId(id);
        if(credits.isEmpty()){
//            log.info("\nNo se encontraron creditos");
            return ResponseEntity.notFound().build();
        }
        List<Credit>creditsWithCustomerData = credits.stream().map(credit -> {
            credit.setCustomer(getCustomerById(credit.getCustomerId()).getBody());
            return credit;
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(creditsWithCustomerData);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Credit> getCreditById (@PathVariable("id") Long id){
        Credit creditDB = creditService.getCredit(id);
        if(creditDB==null){
            return ResponseEntity.badRequest().build();
        }
        creditDB.setCustomer(getCustomerById(creditDB.getCustomerId()).getBody());
        return ResponseEntity.ok().body(creditDB);
    }

    @PostMapping("/registerCredit")
    public ResponseEntity<Credit> createCredit (@RequestBody RBCreditRegistration rbCreditRegistration){
        Credit creditBody = new Credit ();
        creditBody.setCustomerId(rbCreditRegistration.getCustomerId());
        CreditCategory category = new CreditCategory();
            category.setId(rbCreditRegistration.getCategoryId());
        creditBody.setCategory(category);
        creditBody.setCreditNumber(rbCreditRegistration.getCreditNumber());
        return ResponseEntity.ok().body(creditService.createCredit(creditBody));
    }
}
