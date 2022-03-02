package com.nttdata.proyect.creditsservice.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name="tbl_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Credit credit;

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
