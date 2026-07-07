package com.api.moabet.dto.users;

import java.math.BigDecimal;

public record UserResponseDTO(
                String name,
                String email,
                String cpf,
                String phone,
                BigDecimal balance) {
}
