package com.dominium.backend.domain.reservas.repository;

import com.dominium.backend.domain.reservas.*;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.usuario.UsuarioId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    Reserva save(Reserva reserva);

    Optional<Reserva> findById(ReservaId id);

    List<Reserva> buscarPorUsuario(UsuarioId usuarioId);

    List<Reserva> buscarAtivasPorPeriodo(
            AreaComumId areaComumId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    );
}