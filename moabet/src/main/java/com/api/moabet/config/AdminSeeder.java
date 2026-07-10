package com.api.moabet.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.api.moabet.models.User;
import com.api.moabet.models.Wallet;
import com.api.moabet.models.enums.Role;
import com.api.moabet.repository.UserRepository;
import com.api.moabet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String args[]) throws Exception {

        if (userRepository.existsByEmail("admin@moabet.com")) {
            return;
        }
        User user = new User();
        user.setName("Admin");
        user.setEmail("admin@moabet.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setCpf("12699059455");
        user.setPhone("00000000000");
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);
    }
}
