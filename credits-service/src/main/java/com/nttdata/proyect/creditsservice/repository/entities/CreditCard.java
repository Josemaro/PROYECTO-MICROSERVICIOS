package com.nttdata.proyect.creditsservice.repository.entities;

import com.nttdata.proyect.creditsservice.models.Customer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name="tbl_credit_cards")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="limit_credit")
    private Double limitCredit;

    @Column(name = "create_at")
    private LocalDate createAt;

    @Transient
    private Customer customer;

    @PrePersist
    public void prePersist(){
        this.createAt = LocalDate.now();
    }
}
