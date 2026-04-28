package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notification;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import org.springframework.stereotype.Service;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao;

@Service
public class ConsoleNotificacaoService implements NotificacaoService {
    @Override
    public void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        System.out.println("[NOTIFICAÇÃO] [" + tipo + "] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
