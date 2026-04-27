package com.dominium.backend.application.governanca.voto.dto;

public record VotoResponse(
        Long id,
        Long pautaId,
        Long unidadeId,
        Long usuarioId,
        String opcao
) {}