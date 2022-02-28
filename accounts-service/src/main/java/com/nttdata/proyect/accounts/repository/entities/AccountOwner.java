package com.nttdata.proyect.accounts.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nttdata.proyect.accounts.models.Customer;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbl_account_owners")
public class AccountOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Account account;

    @Column(name = "customer_id")
    private Long customerId;

    @Transient
    private Customer customer;
}
