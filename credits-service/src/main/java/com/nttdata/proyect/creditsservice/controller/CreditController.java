package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import com.nttdata.proyect.creditsservice.models.Customer;
import com.nttdata.proyect.creditsservice.models.RBCreditRegistration;
import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.repository.entities.Payment;
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

    @GetMapping("/paymentsByCreditId/{id}")
    public ResponseEntity<List<Payment>> getPaymentsByCreditId(@PathVariable("id") Long id){
        Credit creditDB = getCreditById(id).getBody();
        if(creditDB==null){
            return ResponseEntity.badRequest().build();
        }
        List<Payment>payments = creditService.getPaymentsByCreditId(creditDB.getId());
        return ResponseEntity.ok().body(payments);
    }

    @GetMapping("/paymentsPaidByCreditId/{id}")
    public ResponseEntity<List<Payment>> getPaymentsPaid (@PathVariable("id") Long id){
        List<Payment> payments = getPaymentsByCreditId(id).getBody();
        assert payments != null;
        List<Payment> paidPayments = payments.stream().filter(p->{
            return p.getState().equals("PAID");
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(paidPayments);
    }

    @GetMapping("/paymentsPendingByCreditId/{id}")
    public ResponseEntity<List<Payment>> getPaymentsNotPaid (@PathVariable("id") Long id){
        List<Payment> payments = getPaymentsByCreditId(id).getBody();
        assert payments != null;
        List<Payment> paidPayments = payments.stream().filter(p->{
            return p.getState().equals("PENDING");
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(paidPayments);
    }

    @GetMapping("/payAPayment/{id}")
    public ResponseEntity<Payment> payAPayment (@PathVariable("id") Long id){
        Payment payment = creditService.getPayment(id);
        Payment paymentUpdated = creditService.payAPayment(payment);
        return ResponseEntity.ok().body(paymentUpdated);
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
        Credit creditDB = creditService.createCredit(creditBody);
        if(creditDB==null){
            return ResponseEntity.badRequest().build();
        }
        int numberOfInstalments = creditBody.getNumberOfInstalments();
        for(int i=0;i<numberOfInstalments;i++){
            Payment payment = new Payment();
            payment.setCredit(creditDB);
            payment.setCreateAt(LocalDate.now());
            payment.setState("PENDING");
            payment.setAmount(calcAmountOfPayment(creditDB.getMoneyAmount(),creditDB.getInterestRate(),creditDB.getNumberOfInstalments()));
            creditService.createPayment(payment);
        }

        return ResponseEntity.ok().body(creditDB);
    }

    public double calcAmountOfPayment(Double amountBase, Double interestRate, int numberOfInstaltments){
        return (amountBase/numberOfInstaltments)+((amountBase*interestRate)/numberOfInstaltments);
    }
}
