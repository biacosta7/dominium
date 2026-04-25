package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumService;
import com.dominium.backend.domain.reservas.*;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import com.dominium.backend.domain.reservas.service.PoliticaReserva;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriarReservaUseCase {

    private final ReservaRepository repository;
    private final PoliticaReserva politica;
    private final AreaComumService areaComumService;

    public CriarReservaUseCase(ReservaRepository repository, PoliticaReserva politica,
                               AreaComumService areaComumService) {
        this.repository = repository;
        this.politica = politica;
        this.areaComumService = areaComumService;
    }

    public Reserva executar(Reserva reserva) {

        AreaComum area = areaComumService.buscarArea(reserva.getAreaComumId());

        List<Reserva> existentes =
                repository.buscarAtivasPorPeriodo(
                        reserva.getAreaComumId(),
                        reserva.getDataReserva(),
                        reserva.getHoraInicio(),
                        reserva.getHoraFim()
                );

        politica.validarNovaReserva(reserva, area, existentes);

        reserva.ativar();

        return repository.save(reserva);
    }
}