package com.api.moabet.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.api.moabet.models.enums.Type;

public record DepositResponseDTO(
        BigDecimal amount,
        Long transactionId,
        LocalDateTime createdAt,
        Type type) {
}
