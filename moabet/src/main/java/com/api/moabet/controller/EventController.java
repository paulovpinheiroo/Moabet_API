package com.api.moabet.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.moabet.dto.bet.BetResponseDTO;
import com.api.moabet.dto.event.EventFinishDTO;
import com.api.moabet.dto.event.EventRequestDTO;
import com.api.moabet.dto.event.EventResponseDTO;
import com.api.moabet.service.BetService;
import com.api.moabet.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final BetService betService;
    private final EventService eventService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponseDTO createEvent(@Valid @RequestBody EventRequestDTO eventRequestDTO) {
        return eventService.createEvent(eventRequestDTO);
    }

    @PutMapping("/{id}/finish")
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponseDTO finishEvent(@PathVariable Long id, @Valid @RequestBody EventFinishDTO result) {
        return eventService.finishEvent(id, result);
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvents() {
        return eventService.getAllEvents();
    }


    @GetMapping("/{id}/bets")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BetResponseDTO> getBetsByEventId(@PathVariable Long id) {
        return betService.getBetsByEventId(id);
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
