package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.models.RBCreditRegistration;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            return ResponseEntity.notFound().build();
        }
        List<Credit>credits = creditService.findAllByCustomerId(id);
        if(credits.isEmpty()){
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
        creditBody.setCategory(rbCreditRegistration.getCategoryId());
        creditBody.setCreditCode(rbCreditRegistration.getCreditCode());
        creditBody.setInterestRate(rbCreditRegistration.getInterestRate());
        creditBody.setMoneyAmount(rbCreditRegistration.getMoneyAmount());
        creditBody.setNumberOfInstalments(rbCreditRegistration.getNumberOfInstalments());
        creditBody.setCreateAt(LocalDate.now());
        return ResponseEntity.ok().body(creditService.createCredit(creditBody));
    }
}
