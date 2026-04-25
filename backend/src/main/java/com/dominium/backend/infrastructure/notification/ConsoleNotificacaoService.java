package com.dominium.backend.infrastructure.notification;

import com.dominium.backend.domain.shared.notification.NotificacaoService;
import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificacaoService implements NotificacaoService {
    @Override
    public void enviar(Long usuarioId, String mensagem) {
        System.out.println("[NOTIFICAÇÃO] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
