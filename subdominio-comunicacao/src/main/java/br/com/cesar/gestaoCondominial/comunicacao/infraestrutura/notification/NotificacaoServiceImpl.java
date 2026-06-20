package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notification;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementação concreta do Template Method.
 *
 * Responsabilidade: persistir a notificação no banco de dados.
 * Não realiza despacho externo — a entrega ao usuário é feita via consulta REST.
 */
@Service
@Primary
public class NotificacaoServiceImpl extends NotificacaoServiceTemplate {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoServiceImpl(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Override
    protected void persistir(Notificacao notificacao) {
        notificacaoRepository.save(notificacao);
    }

    @Override
    protected void despachar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        // entrega é feita via consulta: o usuário busca suas notificações pela API
    }
}
