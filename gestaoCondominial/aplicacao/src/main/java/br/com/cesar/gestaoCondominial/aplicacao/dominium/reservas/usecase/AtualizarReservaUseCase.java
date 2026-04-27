package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.areacomum.AreaComumService;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.service.PoliticaReserva;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AtualizarReservaUseCase {

        private final ReservaRepository repository;
        private final PoliticaReserva politica;
        private final AreaComumService areaComumService;

        public AtualizarReservaUseCase(ReservaRepository repository, PoliticaReserva politica,
                        AreaComumService areaComumService) {
                this.repository = repository;
                this.politica = politica;
                this.areaComumService = areaComumService;
        }

        public Reserva executar(
                        ReservaId id,
                        LocalDate novaData,
                        LocalTime novoInicio,
                        LocalTime novoFim) {

                Reserva reserva = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

                AreaComum area = areaComumService.buscarArea(reserva.getAreaComumId());

                // cria uma "nova versão" temporária pra validar
                Reserva versaoAtualizada = Reserva.reconstituir(
                                reserva.getId(),
                                reserva.getAreaComumId(),
                                reserva.getUnidadeId(),
                                reserva.getUsuarioId(),
                                novaData,
                                novoInicio,
                                novoFim,
                                reserva.getStatus());

                List<Reserva> existentes = repository.buscarAtivasPorPeriodo(
                                reserva.getAreaComumId(),
                                novaData,
                                novoInicio,
                                novoFim);

                // ⚠️ remove a própria reserva da lista (evita falso conflito)
                existentes.removeIf(r -> r.getId().equals(reserva.getId()));

                politica.validarNovaReserva(versaoAtualizada, area, existentes);

                // aplica mudança depois de validar
                reserva.atualizarDados(novaData, novoInicio, novoFim);

                return repository.save(reserva);
        }
}