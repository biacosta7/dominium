package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.dto;

public record RegistrarAvaliacaoRequest(
        boolean positiva,
        String comentario
) {}
