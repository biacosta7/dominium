package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.voto.dto;

public record VotoResponse(
        Long id,
        Long pautaId,
        Long unidadeId,
        Long usuarioId,
        String opcao
) {}