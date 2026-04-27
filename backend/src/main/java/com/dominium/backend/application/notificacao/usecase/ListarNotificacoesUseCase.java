package com.dominium.backend.application.notificacao.usecase;

import com.dominium.backend.domain.notificacao.Notificacao;
import com.dominium.backend.domain.notificacao.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarNotificacoesUseCase {

    private final NotificacaoRepository notificacaoRepository;

    public ListarNotificacoesUseCase(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Notificacao> executar(Long usuarioId) {
        return notificacaoRepository.findByUsuarioId(usuarioId);
    }
}
