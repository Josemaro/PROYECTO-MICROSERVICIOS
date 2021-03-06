package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.models.RBCreditCardConsumption;
import com.nttdata.proyect.creditsservice.models.RBCreditCardRegistration;
import com.nttdata.proyect.creditsservice.repository.entities.Consumption;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.CreditCard;
import com.nttdata.proyect.creditsservice.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/credit-card")
public class CreditCardController {
    @Autowired
    CustomerClient customerClient;

    @Autowired
    CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<List<CreditCard>> getAll(){
        List<CreditCard> creditCards = creditCardService.findAllCreditCards();
        List<CreditCard> creditsCardsWithCustomerData = creditCards.stream().map(credit -> {
            credit.setCustomer(customerClient.getCustomerById(credit.getCustomerId()).getBody());
            return credit;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(creditsCardsWithCustomerData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCard>getById(@PathVariable("id") Long id){
        List<CreditCard>creditCards = creditCardService.findAllByCustomerId(id);
        CreditCard creditCardDB = creditCards.get(0);
        creditCardDB.setCustomer(customerClient.getCustomerById(creditCardDB.getId()).getBody());
        return ResponseEntity.ok().body(creditCardDB);
    }

    @GetMapping("/{creditCardId}/consumptions")
    public ResponseEntity<List<Consumption>> getAllConsumptions(@PathVariable("creditCardId")Long id){
        List<Consumption>consumptions = creditCardService.findAllConsumption(id);
        if(consumptions == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(consumptions);
    }


    @PostMapping("/register-credit-card")
    public ResponseEntity<CreditCard> registerCreditCard(@RequestBody RBCreditCardRegistration rbCreditCardRegistration) {
//        Credit creditBody = new Credit();
        CreditCard creditCardBody = new CreditCard();
        Customer customer = customerClient.getCustomerById(rbCreditCardRegistration.getCustomerId()).getBody();
        if(customer==null){
            return ResponseEntity.notFound().build();
        }
        // SOLO PERMITE UN CREDITO POR PERSONA
        if(!creditCardService.canGetACreditCard(customer)){
            return ResponseEntity.badRequest().build();
        }

        creditCardBody.setCreateAt(LocalDate.now());
        creditCardBody.setCustomer(customer);
        creditCardBody.setCustomerId(customer.getId());
        creditCardBody.setLimitCredit(rbCreditCardRegistration.getLimitCredit());
        creditCardBody.setBalance(rbCreditCardRegistration.getLimitCredit());

        CreditCard creditCardDB = creditCardService.registerCreditCard(creditCardBody);
        if(creditCardDB == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(creditCardDB);
    }

    @PostMapping("/{creditCardId}/register-consumption")
    public ResponseEntity<Consumption> registerConsumption(@PathVariable("creditCardId")Long creditCardId,@RequestBody RBCreditCardConsumption rbCreditCardConsumption) {
        CreditCard creditCardDB = creditCardService.findCreditCardById(creditCardId);
        Consumption consumption = new Consumption();
        consumption.setCreditCard(creditCardDB);
        consumption.setAmount(rbCreditCardConsumption.getAmount());
        consumption.setCreateAt(LocalDate.now());
        consumption.setDetail(rbCreditCardConsumption.getDetail());

        Consumption consumptionDB = creditCardService.saveConsumption(creditCardDB,consumption);

        return ResponseEntity.ok().body(consumptionDB);
    }

}
