package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.dto;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto.OpcaoVoto;

public record VotarRequest(
        Long pautaId,
        Long usuarioId,
        Long unidadeId,
        OpcaoVoto opcao
) {}