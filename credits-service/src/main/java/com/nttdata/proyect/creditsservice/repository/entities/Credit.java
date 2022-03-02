package com.nttdata.proyect.creditsservice.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nttdata.proyect.creditsservice.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbl_credit")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_number", unique = true, nullable = false)
    private String creditNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private CreditCategory category;

    @Column(name="customer_id")
    private Long customerId;

    @Transient
    private Customer customer;
}
