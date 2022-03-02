package com.nttdata.proyect.creditsservice.repository.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_credit_categories")
public class CreditCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
