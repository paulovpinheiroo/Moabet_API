package com.api.moabet.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.api.moabet.dto.event.EventFinishDTO;
import com.api.moabet.dto.event.EventRequestDTO;
import com.api.moabet.dto.event.EventResponseDTO;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.Event;
import com.api.moabet.models.enums.StatusEvent;
import com.api.moabet.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {
        private final EventRepository eventRepository;
        private final BetService betService;

        public EventResponseDTO createEvent(EventRequestDTO event) {
                Event newEvent = new Event();
                newEvent.setName(event.name());
                newEvent.setDescription(event.description());
                newEvent.setOdds(event.odds());
                newEvent.setStatus(StatusEvent.OPEN);
                newEvent.setCreatedAt(LocalDateTime.now());
                newEvent.setResult(null);
                Event savedEvent = eventRepository.save(newEvent);
                return new EventResponseDTO(
                                savedEvent.getName(),
                                savedEvent.getDescription(),
                                savedEvent.getOdds(),
                                savedEvent.getStatus(),
                                savedEvent.getResult());
        }

        public EventResponseDTO finishEvent(Long eventId, EventFinishDTO result) {
                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
                event.setStatus(StatusEvent.FINISHED);
                event.setResult(result.result());
                betService.resolveBets(eventId, result.result());
                eventRepository.save(event);
                return new EventResponseDTO(
                                event.getName(),
                                event.getDescription(),
                                event.getOdds(),
                                event.getStatus(),
                                event.getResult());
        }

        public EventResponseDTO getEventById(Long id) {
                return eventRepository.findById(id)
                                .map(event -> new EventResponseDTO(
                                                event.getName(),
                                                event.getDescription(),
                                                event.getOdds(),
                                                event.getStatus(),
                                                event.getResult()))
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found")); // Rertorna null
                                                                                                      // porém o codigo
                                                                                                      // de status HTTP
                                                                                                      // deve ser 404,
                                                                                                      // será tratado
                // na
                // fase de tratamento de erros.
        }

        public List<EventResponseDTO> getAllEvents() {
                return eventRepository.findAll().stream()
                                .map(event -> new EventResponseDTO(
                                                event.getName(),
                                                event.getDescription(),
                                                event.getOdds(),
                                                event.getStatus(),
                                                event.getResult()))
                                .toList();
        }

        public void deleteEvent(Long id) {
                eventRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

                eventRepository.deleteById(id);
        }
}
