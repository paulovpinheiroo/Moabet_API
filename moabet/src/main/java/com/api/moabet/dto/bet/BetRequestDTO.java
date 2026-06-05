package com.api.moabet.dto.bet;

public record BetRequestDTO(
        Long userId,
        Long eventId,
        Double amount) {
}
