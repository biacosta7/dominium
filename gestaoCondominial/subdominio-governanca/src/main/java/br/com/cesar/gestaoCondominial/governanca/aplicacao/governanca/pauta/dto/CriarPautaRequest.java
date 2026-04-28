package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.dto;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoMaioria;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.TipoQuorum;

public record CriarPautaRequest(
        Long assembleiaId,
        String titulo,
        String descricao,
        TipoQuorum quorum,
        TipoMaioria maioria
) {}