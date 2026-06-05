package com.api.moabet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.moabet.models.Bet;

public interface BetRepository extends JpaRepository<Bet, Long> {

}
