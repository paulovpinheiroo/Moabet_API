package com.api.moabet.dto.transanction;

import java.time.LocalDateTime;

import com.api.moabet.models.enums.Type;

public record DepositResponseDTO(
        Double amount,
        Long id_transaction,
        LocalDateTime createdAt,
        Type type) {

}
