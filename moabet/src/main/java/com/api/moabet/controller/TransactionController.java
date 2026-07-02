package com.api.moabet.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.moabet.dto.transaction.DepositRequestDTO;
import com.api.moabet.dto.transaction.DepositResponseDTO;
import com.api.moabet.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{walletId}/deposit")
    public DepositResponseDTO deposit(@PathVariable Long walletId,
            @Valid @RequestBody DepositRequestDTO depositRequestDTO) {
        return transactionService.deposit(walletId, depositRequestDTO);
    }
}
