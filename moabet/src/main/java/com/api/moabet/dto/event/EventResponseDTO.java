package com.api.moabet.dto.event;

import java.math.BigDecimal;

import com.api.moabet.models.enums.Result;
import com.api.moabet.models.enums.StatusEvent;

public record EventResponseDTO(
                String name,
                String description,
                BigDecimal odds,
                StatusEvent status,
                Result result) {

}
