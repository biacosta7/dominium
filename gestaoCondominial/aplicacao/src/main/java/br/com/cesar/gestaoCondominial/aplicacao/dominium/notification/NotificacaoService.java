package br.com.cesar.gestaoCondominial.aplicacao.dominium.notification;

public interface NotificacaoService {
    void enviar(Long usuarioId, String mensagem);
}
