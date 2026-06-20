package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notification;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import org.springframework.stereotype.Service;

/**
 * Implementação concreta do Template Method.
 *
 * Responsabilidade: imprimir a notificação no console (útil para desenvolvimento e testes).
 * Não persiste no banco — sobrescreve apenas o passo de despacho.
 */
@Service
public class ConsoleNotificacaoService extends NotificacaoServiceTemplate {

    @Override
    protected void despachar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        System.out.println("[NOTIFICAÇÃO] [" + tipo + "] Usuário ID: " + usuarioId + " -> " + mensagem);
    }
}
