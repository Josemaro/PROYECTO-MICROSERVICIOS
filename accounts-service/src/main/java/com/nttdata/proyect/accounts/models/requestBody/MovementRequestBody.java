package com.nttdata.proyect.accounts.models.requestBody;

import lombok.Data;

@Data
public class MovementRequestBody {
    Double amount;
    Long typeId;
    Long accountId;
}
