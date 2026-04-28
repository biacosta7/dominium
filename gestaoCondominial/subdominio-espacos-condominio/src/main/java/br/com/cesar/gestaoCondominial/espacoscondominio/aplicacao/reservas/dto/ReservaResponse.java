package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.dto;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;

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