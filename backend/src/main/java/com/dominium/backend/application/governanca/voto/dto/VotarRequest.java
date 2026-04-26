package com.dominium.backend.application.governanca.voto.dto;

import com.dominium.backend.domain.governanca.voto.OpcaoVoto;

public record VotarRequest(
        Long pautaId,
        Long usuarioId,
        Long unidadeId,
        OpcaoVoto opcao
) {}