package com.dominium.backend.service;

import com.dominium.backend.entity.Reserva;
import com.dominium.backend.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    public Reserva criarReserva(Reserva reserva) {

        List<Reserva> conflitos = repository.verificarConflito(
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getEspacoReservado()
        );

        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Já existe uma reserva nesse horário.");
        }

        reserva.setStatus("ATIVA");
        return repository.save(reserva);
    }

    public List<Reserva> listarPorUnidade(Long unidadeId) {
        return repository.findByUnidadeId(unidadeId);
    }

    public Reserva atualizarReserva(Long id, Reserva novaReserva) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setDataReserva(novaReserva.getDataReserva());
        reserva.setHoraInicio(novaReserva.getHoraInicio());
        reserva.setHoraFim(novaReserva.getHoraFim());

        return repository.save(reserva);
    }

    public void cancelarReserva(Long id) {
        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setStatus("CANCELADA");
        repository.save(reserva);
    }


}
