package com.nttdata.proyect.accounts.repository.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nttdata.proyect.accounts.models.Customer;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tbl_account_signers")
public class AccountSigner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Account account;

    @Column(name = "customer_id")
    private Long customerId;

    @Transient
    private Customer customer;
}
