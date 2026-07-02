package com.api.moabet.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.moabet.dto.users.UserRequestDTO;
import com.api.moabet.dto.users.UserResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.User;
import com.api.moabet.models.Wallet;
import com.api.moabet.repository.UserRepository;
import com.api.moabet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getCpf(),
                        user.getPhone()))
                .toList();
    }

    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getCpf(),
                        user.getPhone()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found")); // fase de tratamento de erros.
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.email()).isPresent()) {
            throw new BusinessException("Email already exists");
        }
        User user = new User();
        user.setName(userRequestDTO.name());
        user.setEmail(userRequestDTO.email());
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        user.setCpf(userRequestDTO.cpf());
        user.setPhone(userRequestDTO.phone());
        User savedUser = userRepository.save(user);
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(0.0);
        walletRepository.save(wallet);
        return new UserResponseDTO(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCpf(),
                savedUser.getPhone());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
