package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.dto;

public record EnviarNotificacaoRequest(
        String mensagem,
        String tipo,
        String publico
) {}
