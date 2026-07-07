package com.api.moabet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Bet;
import com.api.moabet.models.enums.StatusBet;

public interface BetRepository extends JpaRepository<Bet, Long> {

    List<Bet> findByEventIdAndStatus(Long eventId, StatusBet status);

    List<Bet> findAllByEventId(Long eventId);
}
