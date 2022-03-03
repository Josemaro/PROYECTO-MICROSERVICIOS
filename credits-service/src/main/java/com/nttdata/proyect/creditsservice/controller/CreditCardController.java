package com.nttdata.proyect.creditsservice.controller;

import com.nttdata.proyect.creditsservice.client.CustomerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit-card")
public class CreditCardController {
    @Autowired
    CustomerClient customerClient;
}
