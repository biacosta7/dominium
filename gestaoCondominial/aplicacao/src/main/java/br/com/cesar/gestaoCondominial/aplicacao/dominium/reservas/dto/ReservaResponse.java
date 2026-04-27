package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;

public record ReservaResponse(
        String id,
        String status
) {
    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId().toString(),
                reserva.getStatus().name()
        );
    }
}