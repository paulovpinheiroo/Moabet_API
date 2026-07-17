package com.api.moabet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.moabet.dto.event.EventFinishDTO;
import com.api.moabet.dto.event.EventRequestDTO;
import com.api.moabet.dto.event.EventResponseDTO;
import com.api.moabet.exception.ResourceNotFoundException;
import com.api.moabet.models.Event;
import com.api.moabet.models.enums.Result;
import com.api.moabet.models.enums.StatusEvent;
import com.api.moabet.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BetService betService;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        // Objeto base reutilizado nos testes
        event = new Event();
        event.setId(1L);
        event.setName("Cruzeiro vs Atlético-MG");
        event.setDescription("Clássico mineiro");
        event.setOdds(BigDecimal.valueOf(1.85));
        event.setStatus(StatusEvent.OPEN);
        event.setCreatedAt(LocalDateTime.now());
    }

    // --- TESTES DO CREATE EVENT ---

    @Test
    void deveCriarEventoComSucesso() {
        // ARRANGE
        EventRequestDTO request = new EventRequestDTO("Cruzeiro vs Atlético-MG", "Clássico mineiro",
                BigDecimal.valueOf(1.85));

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // ACT
        EventResponseDTO response = eventService.createEvent(request);

        // ASSERT
        assertNotNull(response);
        assertEquals("Cruzeiro vs Atlético-MG", response.name());
        assertEquals(StatusEvent.OPEN, response.status());
        assertNull(response.result());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    // --- TESTES DO FINISH EVENT ---

    @Test
    void deveFinalizarEventoComSucesso() {
        // ARRANGE
        Long eventId = 1L;
        EventFinishDTO finishDTO = new EventFinishDTO(Result.WIN); // Resultado vencedor

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        // Como resolveBets retorna void, não precisamos de "when", o Mockito já finge
        // que funcionou por padrão.

        // ACT
        EventResponseDTO response = eventService.finishEvent(eventId, finishDTO);

        // ASSERT
        assertNotNull(response);
        assertEquals(StatusEvent.FINISHED, response.status());
        assertEquals(Result.WIN, response.result());

        // VERIFY: Garante que as apostas foram resolvidas ANTES ou durante o processo e
        // que salvou
        verify(betService, times(1)).resolveBets(eventId, Result.WIN);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void deveLancarExcecaoAoFinalizarEventoInexistente() {
        // ARRANGE
        Long eventId = 999L;
        EventFinishDTO finishDTO = new EventFinishDTO(Result.WIN);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            eventService.finishEvent(eventId, finishDTO);
        });

        assertEquals("Event not found", exception.getMessage());

        // VERIFY: Nunca deve resolve apostas nem salvar se não achar o evento
        verify(betService, never()).resolveBets(anyLong(), any(Result.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    // --- TESTES DO GET EVENT BY ID ---

    @Test
    void deveBuscarEventoPorIdComSucesso() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // ACT
        EventResponseDTO response = eventService.getEventById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals("Cruzeiro vs Atlético-MG", response.name());
    }

    @Test
    void deveLancarExcecaoAoBuscarIdInexistente() {
        // ARRANGE
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.getEventById(999L);
        });
    }

    // --- TESTES DO GET ALL EVENTS ---

    @Test
    void deveRetornarListaDeEventos() {
        // ARRANGE
        when(eventRepository.findAll()).thenReturn(List.of(event));

        // ACT
        List<EventResponseDTO> response = eventService.getAllEvents();

        // ASSERT
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Cruzeiro vs Atlético-MG", response.get(0).name());
    }

    // --- TESTES DO DELETE ---

    @Test
    void deveDeletarEventoComSucesso() {
        // ARRANGE
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // ACT
        eventService.deleteEvent(1L);

        // ASSERT & VERIFY
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarEventoInexistente() {
        // ARRANGE
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.deleteEvent(999L);
        });

        // VERIFY: Nunca deve tentar deletar se não achar o evento
        verify(eventRepository, never()).deleteById(anyLong());
    }
}
