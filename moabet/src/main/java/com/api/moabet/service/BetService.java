package com.api.moabet.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.api.moabet.dto.bet.BetRequestDTO;
import com.api.moabet.dto.bet.BetResponseDTO;
import com.api.moabet.exception.BusinessException;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.Bet;
import com.api.moabet.models.Event;
import com.api.moabet.models.Transaction;
import com.api.moabet.models.User;
import com.api.moabet.models.Wallet;
import com.api.moabet.models.enums.Result;
import com.api.moabet.models.enums.StatusBet;
import com.api.moabet.models.enums.StatusEvent;
import com.api.moabet.models.enums.Type;
import com.api.moabet.repository.BetRepository;
import com.api.moabet.repository.EventRepository;
import com.api.moabet.repository.TransactionRepository;
import com.api.moabet.repository.UserRepository;
import com.api.moabet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BetService {

    private final TransactionService transactionService;
    private final BetRepository betRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final EventRepository eventRepository;
    private final TransactionRepository transactionRepository;

    public BetResponseDTO createBet(BetRequestDTO betRequestDTO) {
        Bet bet = new Bet();
        User user = userRepository.findById(betRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(betRequestDTO.eventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Wallet wallet = walletRepository.findByUserId(betRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        Bet savedBet = null;
        bet.setAmount(betRequestDTO.amount());

        if (wallet.getBalance().compareTo(bet.getAmount()) >= 0) {
            if (event.getStatus() == StatusEvent.OPEN) {
                wallet.setBalance(wallet.getBalance().subtract(bet.getAmount()));
                walletRepository.save(wallet);
                bet.setUser(user);
                bet.setEvent(event);
                bet.setCreatedAt(LocalDateTime.now());
                bet.setStatus(StatusBet.PENDING);
                Transaction transaction = new Transaction();
                transaction.setAmount(betRequestDTO.amount());
                transaction.setType(Type.BET);
                transaction.setCreatedAt(LocalDateTime.now());
                transaction.setWallet(wallet);
                transactionRepository.save(transaction);
                savedBet = betRepository.save(bet);
            } else {
                throw new BusinessException("Não é possível apostar em um evento fechado ou finalizado.");
            }
        } else {
            throw new BusinessException("Saldo insuficiente para realizar a aposta.");
        }

        return new BetResponseDTO(
                savedBet.getId(),
                savedBet.getAmount(),
                savedBet.getStatus(),
                savedBet.getCreatedAt(),
                savedBet.getUser().getId(),
                savedBet.getEvent().getId());
        // Tratar exceções e retornar mensagens de erro apropriadas
    }

    public List<BetResponseDTO> resolveBets(Long eventId, Result result) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("event not found"));
        List<Bet> bets = betRepository.findByEventIdAndStatus(eventId, StatusBet.PENDING);
        List<BetResponseDTO> resolvedBets = new ArrayList<>();
        for (Bet bet : bets) {
            if (event.getResult() == result) {
                // bet ganhou
                bet.setStatus(StatusBet.WON);
                Long userId = bet.getUser().getId();
                Wallet wallet = walletRepository.findByUserId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
                BigDecimal amount = bet.getAmount();
                BigDecimal odd = event.getOdds();
                BigDecimal valor = amount.multiply(odd);
                // chamar o service transaction pra fazer o credito da bet
                transactionService.creditWin(wallet, valor);
                betRepository.save(bet);
            } else {
                bet.setStatus(StatusBet.LOST);
                betRepository.save(bet);
            }
            resolvedBets.add(new BetResponseDTO(
                    bet.getId(),
                    bet.getAmount(),
                    bet.getStatus(),
                    bet.getCreatedAt(),
                    bet.getUser().getId(),
                    bet.getEvent().getId()));
        }

        return resolvedBets;
    }
}
