package com.nttdata.proyect.accounts.models.requestBody;

import lombok.Data;

@Data
public class AddOwnerSignerBody {
    private String customerDni;
    private Long accountId;
}
