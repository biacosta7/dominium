package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.dto;

public record PautaResponse(
        Long id,
        Long assembleiaId,
        String titulo,
        String descricao,
        String status,
        String resultado
) {}