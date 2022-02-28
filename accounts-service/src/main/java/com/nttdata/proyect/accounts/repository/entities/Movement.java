package com.nttdata.proyect.accounts.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbl_movements")
public class Movement {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Account account;

    @Column(name="movements_limit")
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movement_type_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private MovementType type;
}