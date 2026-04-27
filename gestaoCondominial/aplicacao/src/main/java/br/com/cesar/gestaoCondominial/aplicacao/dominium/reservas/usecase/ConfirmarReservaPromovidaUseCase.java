package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository.ReservaRepository;
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
