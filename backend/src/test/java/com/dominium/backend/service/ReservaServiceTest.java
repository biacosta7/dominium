package com.dominium.backend.service;

import com.dominium.backend.entity.Reserva;
import com.dominium.backend.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @InjectMocks
    private ReservaService service;

    @Mock
    private ReservaRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarReservaSemConflito() {

        Reserva reserva = new Reserva();
        reserva.setDataReserva(LocalDate.now());
        reserva.setHoraInicio(LocalTime.of(14,0));
        reserva.setHoraFim(LocalTime.of(18,0));
        reserva.setEspacoReservado("piscina");

        when(repository.verificarConflito(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(repository.save(any())).thenReturn(reserva);

        Reserva resultado = service.criarReserva(reserva);

        assertEquals("ATIVA", resultado.getStatus());
        verify(repository).save(reserva);
    }

    @Test
    void deveLancarErroQuandoHaConflitoMesmoEspaco() {

        Reserva reserva = new Reserva();
        reserva.setDataReserva(LocalDate.now());
        reserva.setHoraInicio(LocalTime.of(14,0));
        reserva.setHoraFim(LocalTime.of(18,0));
        reserva.setEspacoReservado("piscina");

        when(repository.verificarConflito(any(), any(), any(), any()))
                .thenReturn(List.of(new Reserva()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.criarReserva(reserva);
        });

        assertEquals("Já existe uma reserva nesse horário.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void devePermitirMesmoHorarioEspacosDiferentes() {

        Reserva reserva = new Reserva();
        reserva.setDataReserva(LocalDate.now());
        reserva.setHoraInicio(LocalTime.of(14,0));
        reserva.setHoraFim(LocalTime.of(18,0));
        reserva.setEspacoReservado("churrasqueira");

        // sem conflito porque repo já filtra por espaço
        when(repository.verificarConflito(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(repository.save(any())).thenReturn(reserva);

        Reserva resultado = service.criarReserva(reserva);

        assertNotNull(resultado);
        assertEquals("ATIVA", resultado.getStatus());
    }

    @Test
    void deveCancelarReserva() {

        Reserva reserva = new Reserva();
        reserva.setStatus("ATIVA");

        when(repository.findById(1L)).thenReturn(Optional.of(reserva));

        service.cancelarReserva(1L);

        assertEquals("CANCELADA", reserva.getStatus());
        verify(repository).save(reserva);
    }

    @Test
    void deveAtualizarReserva() {

        Reserva existente = new Reserva();
        existente.setDataReserva(LocalDate.now());

        Reserva nova = new Reserva();
        nova.setDataReserva(LocalDate.of(2026,1,1));
        nova.setHoraInicio(LocalTime.of(10,0));
        nova.setHoraFim(LocalTime.of(12,0));

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenReturn(existente);

        Reserva resultado = service.atualizarReserva(1L, nova);

        assertEquals(LocalDate.of(2026,1,1), resultado.getDataReserva());
        assertEquals(LocalTime.of(10,0), resultado.getHoraInicio());
    }
}