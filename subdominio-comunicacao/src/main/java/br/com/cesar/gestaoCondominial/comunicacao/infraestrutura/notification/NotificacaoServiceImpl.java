package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notification;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
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
        br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao tipoDominio =
            br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao.valueOf(tipo.name());
        Notificacao notificacao = Notificacao.criar(usuarioId, mensagem, tipoDominio);
        notificacaoRepository.save(notificacao);
    }
}
