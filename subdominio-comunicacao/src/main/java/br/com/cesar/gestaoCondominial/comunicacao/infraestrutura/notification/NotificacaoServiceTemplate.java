package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notification;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;

/**
 * Template Method — define o esqueleto do algoritmo de envio de notificação.
 *
 * Algoritmo fixo:
 *   1. formatarMensagem  — hook: subclasse pode personalizar o texto
 *   2. criarEntidade     — fixo: instancia Notificacao no domínio
 *   3. persistir         — hook: default não persiste (útil para console/teste)
 *   4. despachar         — abstract: cada canal implementa sua forma de entrega
 *
 * Subclasses concretas:
 *   - NotificacaoServiceImpl  → persiste no banco, sem despacho externo
 *   - ConsoleNotificacaoService → imprime no console, sem persistência
 */
public abstract class NotificacaoServiceTemplate implements NotificacaoService {

    /**
     * Template method — final para garantir que o algoritmo não seja alterado.
     */
    @Override
    public final void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo) {
        String mensagemFinal = formatarMensagem(mensagem, tipo);
        Notificacao notificacao = Notificacao.criar(usuarioId, mensagemFinal, tipo);
        persistir(notificacao);
        despachar(usuarioId, mensagemFinal, tipo);
    }

    /**
     * Hook — subclasse pode sobrescrever para enriquecer/formatar o texto.
     * Padrão: retorna a mensagem sem alteração.
     */
    protected String formatarMensagem(String mensagem, TipoNotificacao tipo) {
        return mensagem;
    }

    /**
     * Hook — subclasse pode sobrescrever para salvar a notificação.
     * Padrão: não persiste (adequado para console e testes).
     */
    protected void persistir(Notificacao notificacao) {
        // sem persistência por padrão
    }

    /**
     * Abstract — cada implementação define como entrega a notificação ao destinatário.
     * Ex: salvar no banco, imprimir no console, enviar e-mail, push notification, etc.
     */
    protected abstract void despachar(Long usuarioId, String mensagem, TipoNotificacao tipo);
}
