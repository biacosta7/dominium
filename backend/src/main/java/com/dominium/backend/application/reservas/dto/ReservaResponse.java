package com.dominium.backend.application.reservas.dto;

import com.dominium.backend.domain.reservas.Reserva;

public record ReservaResponse(
        String id,
        String status
) {
    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getReservaId().toString(),
                reserva.getStatus().name()
        );
    }
}