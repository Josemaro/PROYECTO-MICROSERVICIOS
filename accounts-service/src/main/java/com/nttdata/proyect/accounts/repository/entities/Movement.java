package com.nttdata.proyect.accounts.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Account account;

    @Column(name="amount")
    private Double amount;

    @Column(name = "movement_date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_type_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private MovementType type;


    @PrePersist
    public void prePersist() {
        this.date = new Date();
    }
}
