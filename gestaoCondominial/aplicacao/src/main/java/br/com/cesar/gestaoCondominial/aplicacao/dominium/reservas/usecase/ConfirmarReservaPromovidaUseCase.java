package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.ReservaId;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfirmarReservaPromovidaUseCase {

    private final ReservaRepository repository;

    public ConfirmarReservaPromovidaUseCase(ReservaRepository repository) {
        this.repository = repository;
    }

    public void executar(ReservaId id) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.confirmar();
        repository.save(reserva);
    }
}
