package com.dominium.backend.domain.reservas.service;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.reservas.Reserva;

import java.util.List;

public class PoliticaReserva {

    public void validarNovaReserva(
            Reserva novaReserva,
            AreaComum area,
            List<Reserva> reservasExistentes
    ) {

        if (!area.estaDisponivel()) {
            throw new RuntimeException("Área não disponível");
        }

        boolean conflito = reservasExistentes.stream()
                .anyMatch(r -> r.conflitoCom(novaReserva));

        if (conflito) {
            throw new RuntimeException("Conflito de horário");
        }

        if (!area.temCapacidade(reservasExistentes.size())) {
            throw new RuntimeException("Capacidade máxima atingida");
        }
    }
}