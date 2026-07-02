package com.api.moabet.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DepositRequestDTO(

        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be a positive value") Double amount) {

}
