package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification;

public interface NotificacaoService {
    void enviar(Long usuarioId, String mensagem, TipoNotificacao tipo);
}
