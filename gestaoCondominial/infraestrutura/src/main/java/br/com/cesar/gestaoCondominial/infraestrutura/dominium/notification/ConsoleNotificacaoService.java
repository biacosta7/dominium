package br.com.cesar.gestaoCondominial.infraestrutura.dominium.notification;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.NotificacaoService;
import org.springframework.stereotype.Service;

@Service
public class ConsoleNotificacaoService implements NotificacaoService {
    @Override
    public void enviar(Long usuarioId, String mensagem) {
        System.out.println("[NOTIFICAÇÃO] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
