package com.api.moabet.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.moabet.dto.users.login.UserLoginRequestDTO;
import com.api.moabet.dto.users.login.UserLoginResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.User;
import com.api.moabet.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        User user = userRepository.findByEmail(userLoginRequestDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if (passwordEncoder.matches(userLoginRequestDTO.password(), user.getPassword())) {
            return new UserLoginResponseDTO(tokenService.generateToken(user));
        } else {
            throw new BusinessException("Invalid password");
        }
    }

}
