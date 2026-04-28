package br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.UsuarioId;
import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComumId;

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