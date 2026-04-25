package com.dominium.backend.application.notificacao.usecase;

import com.dominium.backend.domain.notificacao.Notificacao;
import com.dominium.backend.domain.notificacao.repository.NotificacaoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
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
