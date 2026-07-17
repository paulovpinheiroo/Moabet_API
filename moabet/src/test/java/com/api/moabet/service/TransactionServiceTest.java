package com.api.moabet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.moabet.dto.transaction.DepositRequestDTO;
import com.api.moabet.dto.transaction.DepositResponseDTO;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.Transaction;
import com.api.moabet.models.Wallet;
import com.api.moabet.models.enums.Type;
import com.api.moabet.repository.TransactionRepository;
import com.api.moabet.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100.0)); // Saldo inicial de 100
    }

    // --- TESTES DO DEPOSIT ---

    @Test
    void deveRealizarDepositoComSucesso() {
        // ARRANGE
        Long walletId = 1L;
        DepositRequestDTO requestDTO = new DepositRequestDTO(BigDecimal.valueOf(50.0));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(100L);
        savedTransaction.setAmount(BigDecimal.valueOf(50.0));
        savedTransaction.setType(Type.DEPOSIT);
        savedTransaction.setCreatedAt(LocalDateTime.now());
        savedTransaction.setWallet(wallet);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // ACT
        DepositResponseDTO response = transactionService.deposit(walletId, requestDTO);

        // ASSERT
        assertNotNull(response);
        assertEquals(100L, response.transactionId());
        assertEquals(BigDecimal.valueOf(50.0).compareTo(response.amount()), 0);
        assertEquals(Type.DEPOSIT, response.type());
        assertEquals(BigDecimal.valueOf(150.0).compareTo(wallet.getBalance()), 0); // 100 + 50 = 150

        // VERIFY: Garante que atualizou a carteira e salvou a transação no banco
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraNaoForEncontradaNoDeposito() {
        // ARRANGE
        Long walletId = 999L;
        DepositRequestDTO requestDTO = new DepositRequestDTO(BigDecimal.valueOf(50.0));

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.deposit(walletId, requestDTO);
        });

        assertEquals("Wallet not found", exception.getMessage());

        // VERIFY: Se não achou a carteira, não pode salvar nada no banco
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // --- TESTES DO CREDIT WIN ---

    @Test
    void deveCreditarGanhosComSucesso() {
        // ARRANGE
        BigDecimal winAmount = BigDecimal.valueOf(250.0);
        // Como o método creditWin não tem retorno (void), não usamos o "when" para o
        // serviço.
        // Os métodos de save dos repositórios também retornam void/mock padrão, então
        // apenas passamos os parâmetros.

        // ACT
        transactionService.creditWin(wallet, winAmount);

        // ASSERT
        assertEquals(BigDecimal.valueOf(350.0).compareTo(wallet.getBalance()), 0); // 100 inicial + 250 de ganho = 350

        // VERIFY: Garante que a carteira foi salva com o novo saldo e a transação do
        // tipo WIN foi registrada
        verify(walletRepository, times(1)).save(wallet);
        verify(transactionRepository, times(1)).save(argThat(transaction -> transaction.getAmount().equals(winAmount) &&
                transaction.getType() == Type.WIN &&
                transaction.getWallet().equals(wallet)));
    }
}
