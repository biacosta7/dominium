package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarcarComoLidaUseCase {

    private final NotificacaoRepository notificacaoRepository;

    public MarcarComoLidaUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional
    public Notificacao executar(Long notificacaoId, Long usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!notificacao.getUsuarioId().equals(usuarioId)) {
            throw new DomainException("Notificação não pertence a este usuário");
        }

        notificacao.marcarComoLida();
        return notificacaoRepository.save(notificacao);
    }
}
