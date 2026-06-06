package com.api.moabet.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.moabet.dto.bet.BetRequestDTO;
import com.api.moabet.dto.bet.BetResponseDTO;
import com.api.moabet.service.BetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @PostMapping
    public BetResponseDTO createBet(@RequestBody BetRequestDTO betRequestDTO) {
        return betService.createBet(betRequestDTO);
    }
}
