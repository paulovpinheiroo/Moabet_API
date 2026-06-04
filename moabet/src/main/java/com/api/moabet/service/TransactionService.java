package com.api.moabet.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.api.moabet.dto.transanction.DepositRequestDTO;
import com.api.moabet.dto.transanction.DepositResponseDTO;
import com.api.moabet.models.Transaction;
import com.api.moabet.models.Wallet;
import com.api.moabet.models.enums.Type;
import com.api.moabet.repository.TransactionRepository;
import com.api.moabet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public DepositResponseDTO deposit(Long walletId, DepositRequestDTO depositRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(depositRequestDTO.amount());
        transaction.setType(Type.DEPOSIT);
        transaction.setCreatedAt(LocalDateTime.now());
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
        transaction.setWallet(wallet);
        wallet.setBalance(wallet.getBalance() + depositRequestDTO.amount());
        walletRepository.save(wallet);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new DepositResponseDTO(
                savedTransaction.getAmount(),
                savedTransaction.getId(),
                savedTransaction.getCreatedAt(),
                savedTransaction.getType());
    }
}
