package com.api.moabet.dto.transanction;

import java.time.LocalDateTime;

import com.api.moabet.models.enums.Type;

public record DepositResponseDTO(
                Double amount,
                Long transactionId,
                LocalDateTime createdAt,
                Type type) {
}
