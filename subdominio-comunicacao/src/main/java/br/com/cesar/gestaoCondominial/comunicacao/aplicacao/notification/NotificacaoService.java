package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;

public interface NotificacaoService {
    void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo);
}
