package br.com.cesar.gestaoCondominial.infraestrutura.dominium.notification;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.NotificacaoService;
import org.springframework.stereotype.Service;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.TipoNotificacao;

@Service
public class ConsoleNotificacaoService implements NotificacaoService {
    @Override
    public void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        System.out.println("[NOTIFICAÇÃO] [" + tipo + "] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
