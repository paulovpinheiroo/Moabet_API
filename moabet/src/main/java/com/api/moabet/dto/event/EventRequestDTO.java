package com.api.moabet.dto.event;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequestDTO(

        @NotBlank(message = "Name is required") String name,

        String description,

        @NotNull(message = "Odds is required") @DecimalMin(value = "1.01", message = "Odds must be greater than 1.0") Double odds) {

}
