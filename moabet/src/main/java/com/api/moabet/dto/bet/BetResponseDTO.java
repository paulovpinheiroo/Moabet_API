package com.api.moabet.dto.bet;

import java.time.LocalDateTime;

import com.api.moabet.models.enums.StatusBet;

public record BetResponseDTO(
        Long idBet,
        Double amount,
        StatusBet status,
        LocalDateTime createdAt,
        Long userId,
        Long eventId) {
}
