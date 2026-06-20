package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.Reserva;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.FilaDeEsperaRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.repository.MultaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CancelarReservaUseCase {

    private final ReservaRepository repository;
    private final FilaDeEsperaRepository filaRepository;
    private final NotificacaoService notificacaoService;
    private final UnidadeRepository unidadeRepository;
    private final MultaRepository multaRepository;

    public CancelarReservaUseCase(ReservaRepository repository,
            FilaDeEsperaRepository filaRepository,
            NotificacaoService notificacaoService,
            UnidadeRepository unidadeRepository,
            MultaRepository multaRepository) {
        this.repository = repository;
        this.filaRepository = filaRepository;
        this.notificacaoService = notificacaoService;
        this.unidadeRepository = unidadeRepository;
        this.multaRepository = multaRepository;
    }

    public void executar(ReservaId id) {

        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        LocalDateTime dataHoraReserva = LocalDateTime.of(reserva.getDataReserva(), reserva.getHoraInicio());
        if (LocalDateTime.now().plusHours(24).isAfter(dataHoraReserva)) {
            br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa multa = new br.com.cesar.gestaoCondominial.financeiro.dominio.multa.Multa();

            br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade unidade = unidadeRepository
                    .findById(reserva.getUnidadeId())
                    .orElseThrow(() -> new RuntimeException("Unidade não encontrada"));

            multa.setUnidade(unidade);
            multa.setValor(new java.math.BigDecimal("50.00"));
            multa.setDescricao("Multa por cancelamento tardio de reserva");
            multa.setDataCriacao(LocalDateTime.now());
            multa.setStatus(br.com.cesar.gestaoCondominial.financeiro.dominio.multa.StatusMulta.ABERTA);
            multa.setTipoValor(br.com.cesar.gestaoCondominial.financeiro.dominio.multa.TipoValorMulta.FIXO);
            multa.setReincidencia(0);

            multaRepository.save(multa);
        }

        Optional<FilaDeEspera> proximo = filaRepository.buscarProximoNaFila(
                reserva.getAreaComumId(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim());

        if (proximo.isPresent()) {
            FilaDeEspera fila = proximo.get();
            fila.setStatus(FilaDeEspera.StatusFila.PROMOVIDO);
            filaRepository.salvar(fila);

            UnidadeId unidadeId = unidadeRepository.findAll().stream()
                    .filter(u -> (u.getInquilino() != null && u.getInquilino().getId().equals(fila.getUsuarioId().getValor()))
                            || (u.getProprietario() != null && u.getProprietario().getId().equals(fila.getUsuarioId().getValor())))
                    .map(u -> u.getId())
                    .findFirst()
                    .orElse(null);

            Reserva novaReserva = Reserva.criar(
                    ReservaId.novo(),
                    fila.getAreaComumId(),
                    unidadeId,
                    fila.getUsuarioId(),
                    fila.getDataDesejada(),
                    fila.getHoraInicio(),
                    fila.getHoraFim());
            novaReserva.ativar();

            // Remove the old reservation completely as requested
            repository.delete(reserva.getId());
            repository.save(novaReserva);

            notificacaoService.enviar(fila.getUsuarioId().getValor(),
                    "Sua reserva para a área " + reserva.getAreaComumId().getValor() +
                            " foi promovida da fila de espera!",
                    br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao.PROMOCAO_LISTA_ESPERA);
        } else {
            reserva.cancelar();
            repository.save(reserva);
        }
    }
}