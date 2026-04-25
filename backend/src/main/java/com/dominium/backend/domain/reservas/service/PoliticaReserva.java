package com.dominium.backend.domain.reservas.service;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.exception.AreaNaoDisponivelException;
import com.dominium.backend.domain.reservas.exception.CapacidadeExcedidaException;
import com.dominium.backend.domain.reservas.exception.ConflitoReservaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoliticaReserva {

    public void validarNovaReserva(
            Reserva novaReserva,
            AreaComum area,
            List<Reserva> reservasExistentes
    ) {

        if (!area.estaDisponivel()) {
            throw new AreaNaoDisponivelException();
        }

        boolean conflito = reservasExistentes.stream()
                .anyMatch(r -> r.conflitoCom(novaReserva));

        if (conflito) {
            throw new ConflitoReservaException();
        }

        if (!area.temCapacidade(reservasExistentes.size() + 1)) {
            throw new CapacidadeExcedidaException();
        }
    }
}