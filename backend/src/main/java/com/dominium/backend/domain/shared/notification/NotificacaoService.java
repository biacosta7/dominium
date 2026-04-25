package com.dominium.backend.domain.shared.notification;

public interface NotificacaoService {
    void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo);
}
