package com.dominium.backend.application.reservas.usecase;

import com.dominium.backend.domain.reservas.repository.FilaDeEsperaRepository;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.NotificacaoService;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.UsuarioId;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CancelarReservaUseCase {

    private final ReservaRepository repository;
    private final FilaDeEsperaRepository filaRepository;
    private final NotificacaoService notificacaoService;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public CancelarReservaUseCase(ReservaRepository repository, 
                                  FilaDeEsperaRepository filaRepository, 
                                  NotificacaoService notificacaoService,
                                  UnidadeRepository unidadeRepository,
                                  UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.filaRepository = filaRepository;
        this.notificacaoService = notificacaoService;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void executar(ReservaId id) {

        Reserva reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.cancelar();
        repository.save(reserva);

        // Promoção automática
        Optional<FilaDeEspera> proximo = filaRepository.buscarProximoNaFila(
                reserva.getAreaComumId(), 
                reserva.getDataReserva(), 
                reserva.getHoraInicio(), 
                reserva.getHoraFim()
        );

        proximo.ifPresent(fila -> {
            fila.setStatus(FilaDeEspera.StatusFila.PROMOVIDO);
            filaRepository.salvar(fila);

            UnidadeId unidadeId = unidadeRepository.findAll().stream()
                    .filter(u -> u.getInquilino() != null && u.getInquilino().getId().equals(fila.getUsuarioId()))
                    .map(u -> u.getId())
                    .findFirst()
                    .orElse(null);

            Reserva novaReserva = Reserva.promoverDeFila(
                    ReservaId.novo(),
                    fila.getAreaComumId(),
                    unidadeId,
                    new UsuarioId(fila.getUsuarioId()),
                    fila.getDataDesejada(),
                    fila.getHoraInicio(),
                    fila.getHoraFim()
            );
            
            // Nota: a busca da unidade acima é simplificada. Em um sistema real, a fila deveria guardar a UnidadeId.
            // Para este exercício, vamos assumir que conseguimos recuperar.
            
            repository.save(novaReserva);
            
            notificacaoService.enviar(fila.getUsuarioId(), 
                "Sua reserva para a área " + reserva.getAreaComumId().getValor() + 
                " foi promovida da fila de espera! Você tem 24h para confirmar.");
        });
    }
}