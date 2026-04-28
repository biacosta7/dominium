package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.areacomum.AreaComumService;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.service.PoliticaReserva;

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

        areaComumService.validarDisponibilidade(area);

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