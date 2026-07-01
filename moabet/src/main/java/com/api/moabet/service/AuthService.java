package com.api.moabet.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.moabet.dto.users.login.UserLoginRequestDTO;
import com.api.moabet.models.User;
import com.api.moabet.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(UserLoginRequestDTO userLoginRequestDTO) {
        User user = userRepository.findByEmail(userLoginRequestDTO.email())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        if (passwordEncoder.matches(userLoginRequestDTO.password(), user.getPassword())) {
            // TODO: lança token JWT
        } else {
            // exceção
        }
    }

}
