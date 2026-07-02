package com.api.moabet.dto.users;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters long") String password,

        @NotBlank(message = "CPF is required") @CPF(message = "Invalid CPF format") String cpf,

        @Size(max = 15, message = "Phone number must be at most 15 characters long") String phone) {
}
