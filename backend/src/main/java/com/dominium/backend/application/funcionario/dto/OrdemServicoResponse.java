package com.dominium.backend.application.funcionario.dto;

import com.dominium.backend.domain.funcionario.OrdemServico;

import java.time.LocalDate;

public record OrdemServicoResponse(
        String id,
        String descricao,
        String funcionarioId,
        String status,
        LocalDate dataInicio,
        LocalDate dataFim
) {
    public static OrdemServicoResponse from(OrdemServico os) {
        return new OrdemServicoResponse(
                os.getId().getValor(),
                os.getDescricao(),
                os.getFuncionarioId().getValor(),
                os.getStatus().name(),
                os.getDataInicio(),
                os.getDataFim()
        );
    }
}
