package com.api.moabet.dto.bet;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BetRequestDTO(

                @NotNull(message = "Event ID is required") Long eventId,

                @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal amount) {
}
