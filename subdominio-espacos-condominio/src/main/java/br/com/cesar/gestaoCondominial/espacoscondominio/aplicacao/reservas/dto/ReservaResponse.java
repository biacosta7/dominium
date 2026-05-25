package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        String id,
        String status,
        Long areaComumId,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim
) {
    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId().toString(),
                reserva.getStatus().name(),
                reserva.getAreaComumId().getValor(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim());
    }
}
