package com.api.moabet.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.api.moabet.dto.bet.BetRequestDTO;
import com.api.moabet.dto.bet.BetResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.models.Bet;
import com.api.moabet.models.Event;
import com.api.moabet.models.Transaction;
import com.api.moabet.models.User;
import com.api.moabet.models.Wallet;
import com.api.moabet.models.enums.StatusBet;
import com.api.moabet.models.enums.StatusEvent;
import com.api.moabet.repository.BetRepository;
import com.api.moabet.repository.EventRepository;
import com.api.moabet.repository.TransactionRepository;
import com.api.moabet.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class BetServiceTest {
    @Mock
    private BetRepository betRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BetService betService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private User user;
    private BetRequestDTO betRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        betRequestDTO = new BetRequestDTO(100L, BigDecimal.valueOf(100.0));
        // Configura o mock do Spring Security globalmente para os testes
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void deveCriarApostaComSucesso() {
        // ARRANGE
        Event event = new Event();
        event.setId(100L);
        event.setStatus(StatusEvent.OPEN);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100.0)); // Saldo maior que os 100.0 da aposta

        Bet savedBet = new Bet();
        savedBet.setId(100L);
        savedBet.setAmount(BigDecimal.valueOf(100.0));
        savedBet.setStatus(StatusBet.PENDING);
        savedBet.setCreatedAt(LocalDateTime.now());
        savedBet.setUser(user);
        savedBet.setEvent(event);

        Mockito.when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        Mockito.when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        Mockito.when(betRepository.save(Mockito.any(Bet.class))).thenReturn(savedBet);

        // ACT
        BetResponseDTO response = betService.createBet(betRequestDTO);

        // ASSERT
        Assertions.assertNotNull(response);
        Assertions.assertEquals(100L, response.eventId());
        Assertions.assertEquals(BigDecimal.valueOf(100.0), response.amount());
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(wallet.getBalance())); // 100 - 100 = 0

        // VERIFY: Garante que os repositórios salvaram as entidades
        Mockito.verify(walletRepository, Mockito.times(1)).save(wallet);
        Mockito.verify(transactionRepository, Mockito.times(1)).save(Mockito.any(Transaction.class));
        Mockito.verify(betRepository, Mockito.times(1)).save(Mockito.any(Bet.class));
    }

    @Test
    void deveLancarExcecaoQuandoSaldoForInsuficiente() {
        // ARRANGE
        Event event = new Event();
        event.setStatus(StatusEvent.OPEN);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(10.0)); // Menor que os 100.0 necessários

        Mockito.when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        Mockito.when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        // ACT & ASSERT
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            betService.createBet(betRequestDTO);
        });

        Assertions.assertEquals("Saldo insuficiente para realizar a aposta.", exception.getMessage());

        // VERIFY: Garante que NENHUM save foi chamado
        Mockito.verify(walletRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(betRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoEstiverAberto() {
        // ARRANGE
        Event event = new Event();
        event.setStatus(StatusEvent.CLOSED); // Evento fechado

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100.0)); // Saldo ok

        Mockito.when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        Mockito.when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));

        // ACT & ASSERT
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            betService.createBet(betRequestDTO);
        });

        Assertions.assertEquals("Não é possível apostar em um evento fechado ou finalizado.", exception.getMessage());

        // VERIFY: Saldo não deve mudar e dados não devem ser salvos
        Mockito.verify(walletRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(betRepository, Mockito.never()).save(Mockito.any());
    }

    @AfterEach
    void tearDown() {
        // Limpa o contexto de segurança após cada teste
        SecurityContextHolder.clearContext();
    }
}
