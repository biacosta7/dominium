package br.com.cesar.gestaoCondominial.infraestrutura.dominium.notification;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.notification.TipoNotificacao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class NotificacaoServiceImpl implements NotificacaoService {

    @Override
    public void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        System.out.println("[NOTIFICAÇÃO] [" + tipo + "] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
