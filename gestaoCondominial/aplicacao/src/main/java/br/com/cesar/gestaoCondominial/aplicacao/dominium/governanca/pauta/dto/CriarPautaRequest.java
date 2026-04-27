package com.dominium.backend.application.governanca.pauta.dto;

import com.dominium.backend.domain.governanca.pauta.TipoMaioria;
import com.dominium.backend.domain.governanca.pauta.TipoQuorum;

public record CriarPautaRequest(
        Long assembleiaId,
        String titulo,
        String descricao,
        TipoQuorum quorum,
        TipoMaioria maioria
) {}