package com.dominium.backend.application.notificacao.dto;

import com.dominium.backend.domain.notificacao.Notificacao;

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
