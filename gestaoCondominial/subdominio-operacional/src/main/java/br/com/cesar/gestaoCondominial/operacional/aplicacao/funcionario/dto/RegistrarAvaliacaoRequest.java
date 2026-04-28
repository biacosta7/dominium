package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.dto;

public record RegistrarAvaliacaoRequest(
        boolean positiva,
        String comentario
) {}
