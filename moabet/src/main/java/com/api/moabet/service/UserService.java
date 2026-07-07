package com.api.moabet.service;

import java.math.BigDecimal;
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
                .map(user -> {
                    Wallet wallet = walletRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
                    return new UserResponseDTO(
                            user.getName(),
                            user.getEmail(),
                            user.getCpf(),
                            user.getPhone(),
                            wallet.getBalance());
                })
                .toList();
    }

    public UserResponseDTO getUserById(Long id) {
        Wallet wallet = walletRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getName(),
                        user.getEmail(),
                        user.getCpf(),
                        user.getPhone(),
                        wallet.getBalance()))
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
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
        return new UserResponseDTO(
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCpf(),
                savedUser.getPhone(),
                wallet.getBalance());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
