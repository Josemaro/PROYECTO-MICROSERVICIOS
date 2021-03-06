package com.nttdata.proyect.accounts.models.requestBody;

import com.nttdata.proyect.accounts.repository.entities.Account;
import lombok.Data;

@Data
public class RegistrationRequestBody {
    private String customerDni;
    private Double balance;
    private String accountNumber;
    private Long accountTypeId;
}
