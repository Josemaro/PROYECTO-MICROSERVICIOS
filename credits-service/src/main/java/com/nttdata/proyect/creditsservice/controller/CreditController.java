package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.repository.entities.Credit;
import com.nttdata.proyect.creditsservice.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/credits")
public class CreditController {
    @Autowired
    CreditService creditService;

    @GetMapping
    public ResponseEntity<List<Credit>> getAllCredits (){
        List<Credit>credits = creditService.findAllCredits();
        return ResponseEntity.ok().body(credits);
    }
}
