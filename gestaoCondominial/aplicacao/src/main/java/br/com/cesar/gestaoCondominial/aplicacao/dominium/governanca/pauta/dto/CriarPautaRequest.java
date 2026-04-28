package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.pauta.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.TipoQuorum;

public record CriarPautaRequest(
        Long assembleiaId,
        String titulo,
        String descricao,
        TipoQuorum quorum,
        TipoMaioria maioria
) {}