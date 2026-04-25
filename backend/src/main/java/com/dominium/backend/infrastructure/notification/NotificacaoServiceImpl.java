package com.dominium.backend.infrastructure.notification;

import com.dominium.backend.domain.notificacao.Notificacao;
import com.dominium.backend.domain.notificacao.repository.NotificacaoRepository;
import com.dominium.backend.domain.shared.notification.NotificacaoService;
import com.dominium.backend.domain.shared.notification.TipoNotificacao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoServiceImpl(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Override
    public void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        Notificacao notificacao = Notificacao.criar(usuarioId, mensagem, tipo);
        notificacaoRepository.save(notificacao);
    }
}
