package com.dominium.backend.application.funcionario.dto;

public record RegistrarAvaliacaoRequest(
        boolean positiva,
        String comentario
) {}
