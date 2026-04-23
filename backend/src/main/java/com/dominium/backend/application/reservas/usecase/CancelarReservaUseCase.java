package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.ReservaId;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;

public class CancelarReservaUseCase {

    private final ReservaRepository repository;

    public CancelarReservaUseCase(ReservaRepository repository) {
        this.repository = repository;
    }

    public void executar(ReservaId id) {

        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.cancelar();

        repository.save(reserva);
    }
}
