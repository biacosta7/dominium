package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.dto;

import java.time.LocalDate;

public record CriarOrdemServicoRequest(
        String funcionarioId,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
