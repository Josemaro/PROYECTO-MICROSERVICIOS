package com.nttdata.proyect.accounts.models;

import lombok.Data;

@Data
public class Customer {

    private Long id;

    private String numberID;

    private String firstName;

    private String lastName;

    private String email;

    private CustomerCategory category;

    private String state;
}
