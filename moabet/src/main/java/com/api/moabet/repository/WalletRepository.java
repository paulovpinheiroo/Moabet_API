package com.api.moabet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);

}
