package com.api.moabet.dto.users;

public record UserResponseDTO(
        String name,
        String email,
        String cpf,
        String phone) {
}
