package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.voto.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto.OpcaoVoto;

public record VotarRequest(
        Long pautaId,
        Long usuarioId,
        Long unidadeId,
        OpcaoVoto opcao
) {}