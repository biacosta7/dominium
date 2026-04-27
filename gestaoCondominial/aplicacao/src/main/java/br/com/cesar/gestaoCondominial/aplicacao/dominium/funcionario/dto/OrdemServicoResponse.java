package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.OrdemServico;

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
                os.getId().getValor().toString(),
                os.getDescricao(),
                os.getFuncionarioId().getValor().toString(),
                os.getStatus().name(),
                os.getDataInicio(),
                os.getDataFim()
        );
    }
}
