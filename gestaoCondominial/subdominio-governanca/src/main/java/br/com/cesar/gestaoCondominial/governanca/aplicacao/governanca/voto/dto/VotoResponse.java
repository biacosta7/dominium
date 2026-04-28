package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.voto.dto;

public record VotoResponse(
        Long id,
        Long pautaId,
        Long unidadeId,
        Long usuarioId,
        String opcao
) {}