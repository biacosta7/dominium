package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.dto;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;

import java.time.LocalDateTime;

public record NotificacaoResponse(
        Long id,
        Long usuarioId,
        String mensagem,
        String tipo,
        boolean lida,
        LocalDateTime criadaEm
) {
    public static NotificacaoResponse from(Notificacao n) {
        return new NotificacaoResponse(
                n.getId(),
                n.getUsuarioId(),
                n.getMensagem(),
                n.getTipo().name(),
                n.isLida(),
                n.getCriadaEm()
        );
    }
}
