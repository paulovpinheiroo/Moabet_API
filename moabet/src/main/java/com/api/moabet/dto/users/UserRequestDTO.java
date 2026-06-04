package com.api.moabet.dto.users;

public record UserRequestDTO(
    String name,
    String email,
    String password,
    String cpf,
    String phone
) {
}
