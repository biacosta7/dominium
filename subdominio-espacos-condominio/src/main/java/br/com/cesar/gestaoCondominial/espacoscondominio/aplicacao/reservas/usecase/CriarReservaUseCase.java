package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.areacomum.AreaComumService;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.service.PoliticaReserva;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriarReservaUseCase {

    private final ReservaRepository repository;
    private final PoliticaReserva politica;
    private final AreaComumService areaComumService;
    private final UnidadeRepository unidadeRepository;

    public CriarReservaUseCase(ReservaRepository repository, PoliticaReserva politica,
            AreaComumService areaComumService,
            UnidadeRepository unidadeRepository) {
        this.repository = repository;
        this.politica = politica;
        this.areaComumService = areaComumService;
        this.unidadeRepository = unidadeRepository;
    }

    public Reserva executar(Reserva reserva) {

        AreaComum area = areaComumService.buscarArea(reserva.getAreaComumId());

        areaComumService.validarDisponibilidade(area);

        List<Reserva> existentes = repository.buscarAtivasPorPeriodo(
                reserva.getAreaComumId(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim());

        br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade unidade = unidadeRepository
                .findById(reserva.getUnidadeId())
                .orElseThrow(() -> new DomainException("Unidade não encontrada."));

        if (unidade
                .getStatus() == br.com.cesar.gestaoCondominial.moradores.dominio.unidade.StatusAdimplencia.INADIMPLENTE) {
            throw new DomainException("Unidade inadimplente não pode realizar reservas.");
        }

        long reservasNoMes = repository.buscarPorUsuario(reserva.getUsuarioId()).stream()
                .filter(r -> r.getDataReserva().getMonth() == reserva.getDataReserva().getMonth()
                        && r.getDataReserva().getYear() == reserva.getDataReserva().getYear())
                .count();
        if (reservasNoMes >= 2) {
            throw new DomainException("Limite mensal de reservas atingido para esta unidade.");
        }

        politica.validarNovaReserva(reserva, area, existentes);

        reserva.ativar();

        return repository.save(reserva);
    }
}