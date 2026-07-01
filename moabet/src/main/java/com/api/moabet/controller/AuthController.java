package com.api.moabet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.moabet.dto.users.UserRequestDTO;
import com.api.moabet.dto.users.UserResponseDTO;
import com.api.moabet.dto.users.login.UserLoginRequestDTO;
import com.api.moabet.dto.users.login.UserLoginResponseDTO;
import com.api.moabet.service.AuthService;
import com.api.moabet.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public UserResponseDTO registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @PostMapping("/login")
    public UserLoginResponseDTO loginUser(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return authService.login(userLoginRequestDTO);
    }
}
