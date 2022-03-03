package com.nttdata.proyect.creditsservice.models;

import lombok.Data;

@Data
public class RBCreditRegistration {

    private String creditCode;
    private Double interestRate;
    private Double moneyAmount;
    private int numberOfInstalments;
    private Long customerId;
}
