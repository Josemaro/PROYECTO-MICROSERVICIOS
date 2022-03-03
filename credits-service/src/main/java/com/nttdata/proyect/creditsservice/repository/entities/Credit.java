package com.nttdata.proyect.creditsservice.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nttdata.proyect.creditsservice.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbl_credits")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //1 -> personal, 2->business
    @Column(name = "category")
    private Long category;

    @Column(name = "credit_code", unique = true, nullable = false)
    private String creditCode;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "money_amount")
    private Double moneyAmount;

    //NUMERO DE CUOTAS O PAGOS
    @Column(name = "number_of_instalments")
    private int numberOfInstalments;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name = "create_at")
    private LocalDate createAt;

    @Transient
    private Customer customer;


}
