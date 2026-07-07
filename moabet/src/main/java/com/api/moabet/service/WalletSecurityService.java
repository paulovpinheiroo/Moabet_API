package com.api.moabet.service;

import org.springframework.stereotype.Service;

import com.api.moabet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service("walletSecurityService")
@RequiredArgsConstructor
public class WalletSecurityService {
    private final WalletRepository walletRepository;

    public Boolean isOwner(Long walletId, Long userId) {
        return walletRepository.findById(walletId)
        .map(wallet -> wallet.getUser().getId().equals(userId))
                .orElse(false);
    }

}
