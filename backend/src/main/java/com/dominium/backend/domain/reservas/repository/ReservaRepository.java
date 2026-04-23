package com.dominium.backend.domain.reservas.repository;

import com.dominium.backend.domain.reservas.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    Reserva save(Reserva reserva);

    Optional<Reserva> findById(Long id);

    List<Reserva> listPorUnidade(Long unidadeId);

    long contarReservasAtivas(
            LocalDate dataReserva,
            LocalTime horaInicio,
            LocalTime horaFim,
            Long areaId
    );
}
