package com.dominium.backend.application.funcionario.dto;

import java.time.LocalDate;

public record CriarOrdemServicoRequest(
        String funcionarioId,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataFim
) {}
