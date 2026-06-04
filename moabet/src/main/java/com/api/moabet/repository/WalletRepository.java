package com.api.moabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
