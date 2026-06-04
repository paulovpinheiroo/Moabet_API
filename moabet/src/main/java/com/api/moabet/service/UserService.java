package com.api.moabet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api.moabet.dto.users.UserRequestDTO;
import com.api.moabet.dto.users.UserResponseDTO;
import com.api.moabet.models.User;
import com.api.moabet.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getCpf(),
                        user.getPhone()))
                .toList();
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setName(userRequestDTO.name());
        user.setEmail(userRequestDTO.email());
        user.setPassword(userRequestDTO.password());
        user.setCpf(userRequestDTO.cpf());
        user.setPhone(userRequestDTO.phone());
        User savedUser = userRepository.save(user);
        return new UserResponseDTO(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCpf(),
                savedUser.getPhone());
    }

}
