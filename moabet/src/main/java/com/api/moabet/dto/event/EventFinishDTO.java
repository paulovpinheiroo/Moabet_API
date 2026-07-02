package com.api.moabet.dto.event;

import com.api.moabet.models.enums.Result;

import jakarta.validation.constraints.NotNull;

public record EventFinishDTO(
                @NotNull(message = "Result is required") Result result) {

}
