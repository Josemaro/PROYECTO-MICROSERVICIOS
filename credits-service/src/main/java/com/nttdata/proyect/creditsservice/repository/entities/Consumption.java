package com.nttdata.proyect.creditsservice.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbl_consumptions")
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private CreditCard creditCard;

    @Column(name = "amount")
    private Double amount;

    //"PENDING" or "PAID"
    @Column(name = "state")
    private String state;

    @Column(name = "create_at")
    private LocalDate createAt;

    @PrePersist
    public void prePersist(){
        this.state = "PENDING";
    }
}
