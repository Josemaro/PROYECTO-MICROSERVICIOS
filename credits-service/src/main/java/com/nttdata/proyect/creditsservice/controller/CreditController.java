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
import java.util.stream.IntStream;

@RestController
@RequestMapping("/credits")
public class CreditController {
    @Autowired
    CreditService creditService;
    @Autowired
    CustomerClient customerClient;

    @GetMapping
    public ResponseEntity<List<Credit>> getAllCredits() {
        List<Credit> credits = creditService.findAllCredits();
        List<Credit> creditsWithCustomerData = credits.stream().map(credit -> {
            credit.setCustomer(customerClient.getCustomerById(credit.getCustomerId()).getBody());
            return credit;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(creditsWithCustomerData);
    }

    @GetMapping("/customer-credits/{customerId}")
    public ResponseEntity<List<Credit>> getAllCreditsOfOneCustomer(@PathVariable("customerId") Long id) {

        Customer customer = customerClient.getCustomerById(id).getBody();
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        List<Credit> credits = creditService.findAllByCustomerId(id);
        if (credits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Credit> creditsWithCustomerData = credits.stream().map(credit -> {
            credit.setCustomer(customerClient.getCustomerById(credit.getCustomerId()).getBody());
            return credit;
        }).collect(Collectors.toList());

        return ResponseEntity.ok().body(creditsWithCustomerData);
    }

    @GetMapping("/{creditId}")
    public ResponseEntity<Credit> getCreditById(@PathVariable("creditId") Long id) {
        Credit creditDB = creditService.getCredit(id);
        if (creditDB == null) {
            return ResponseEntity.badRequest().build();
        }
        creditDB.setCustomer(customerClient.getCustomerById(creditDB.getCustomerId()).getBody());
        return ResponseEntity.ok().body(creditDB);
    }

    @GetMapping("/payments-credit/{creditId}")
    public ResponseEntity<List<Payment>> getPaymentsByCreditId(@PathVariable("creditId") Long id) {
        Credit creditDB = getCreditById(id).getBody();
        if (creditDB == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Payment> payments = creditService.getPaymentsByCreditId(creditDB.getId());
        return ResponseEntity.ok().body(payments);
    }

    @GetMapping("/payments-paid/{creditId}")
    public ResponseEntity<List<Payment>> getPaymentsPaid(@PathVariable("creditId") Long id) {
        List<Payment> payments = getPaymentsByCreditId(id).getBody();
        assert payments != null;
        List<Payment> paidPayments = payments.stream().filter(p -> p.getState().equals("PAID")).collect(Collectors.toList());
        return ResponseEntity.ok().body(paidPayments);
    }

    @GetMapping("/payments-pending/{creditId}")
    public ResponseEntity<List<Payment>> getPaymentsNotPaid(@PathVariable("creditId") Long id) {
        List<Payment> payments = getPaymentsByCreditId(id).getBody();
        assert payments != null;
        List<Payment> pendingPayments = payments.stream().filter(p -> p.getState().equals("PENDING")).collect(Collectors.toList());
        return ResponseEntity.ok().body(pendingPayments);
    }

    @GetMapping("/pay/{paymentId}")
    public ResponseEntity<Payment> payAPayment(@PathVariable("paymentId") Long id) {
        Payment payment = creditService.getPayment(id);
        Payment paymentUpdated = creditService.payAPayment(payment);
        return ResponseEntity.ok().body(paymentUpdated);
    }

    @PostMapping("/register-credit")
    public ResponseEntity<Credit> createCredit(@RequestBody RBCreditRegistration rbCreditRegistration) {
        Credit creditBody = new Credit();
        Customer customer = customerClient.getCustomerById(rbCreditRegistration.getCustomerId()).getBody();
        if(customer==null){
            return ResponseEntity.notFound().build();
        }
        // SOLO PERMITE UN CREDITO POR PERSONA
        if(!creditService.canGetACredit(customer)){
            return ResponseEntity.badRequest().build();
        }

        creditBody.setCustomerId(rbCreditRegistration.getCustomerId());
        creditBody.setCategory(customer.getCategory().getId());
        creditBody.setCreditCode(rbCreditRegistration.getCreditCode());
        creditBody.setInterestRate(rbCreditRegistration.getInterestRate());
        creditBody.setMoneyAmount(rbCreditRegistration.getMoneyAmount());
        creditBody.setNumberOfInstalments(rbCreditRegistration.getNumberOfInstalments());
        creditBody.setCreateAt(LocalDate.now());
        Credit creditDB = creditService.createCredit(creditBody);
        if (creditDB == null) {
            return ResponseEntity.badRequest().build();
        }
        int numberOfInstalments = creditBody.getNumberOfInstalments();
        IntStream.range(0, numberOfInstalments).mapToObj(i -> new Payment()).forEach(payment -> {
            payment.setCredit(creditDB);
            payment.setCreateAt(LocalDate.now());
            payment.setState("PENDING");
            payment.setAmount(calcAmountOfPayment(creditDB.getMoneyAmount(), creditDB.getInterestRate(), creditDB.getNumberOfInstalments()));
            creditService.createPayment(payment);
        });

        return ResponseEntity.ok().body(creditDB);
    }

    public double calcAmountOfPayment(Double amountBase, Double interestRate, int numberOfInstaltments) {
        return (amountBase / numberOfInstaltments) + ((amountBase * interestRate) / numberOfInstaltments);
    }
}
