package com.nttdata.proyect.creditsservice.models;

import lombok.Data;

@Data
public class RBCreditRegistration {
    private Long customerId;
    private Long categoryId;
    private String creditNumber;
}
