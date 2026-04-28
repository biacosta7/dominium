package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.dto;

import java.time.LocalDate;

public record CriarOrdemServicoRequest(
        String funcionarioId,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
