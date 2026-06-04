package com.api.moabet.dto.event;

import com.api.moabet.models.enums.Result;
import com.api.moabet.models.enums.StatusEvent;

public record EventResponseDTO(
        String name,
        String description,
        Double odds,
        StatusEvent status,
        Result result) {

}
