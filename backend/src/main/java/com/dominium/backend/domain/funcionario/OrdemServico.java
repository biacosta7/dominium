package com.dominium.backend.domain.funcionario;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrdemServico {

    private OrdemServicoId id;
    private String descricao;
    private FuncionarioId funcionarioId;
    private StatusOrdemServico status;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public static OrdemServico criar(OrdemServicoId id, String descricao, FuncionarioId funcionarioId,
                                     LocalDate dataInicio, LocalDate dataFim) {
        OrdemServico os = new OrdemServico();
        os.id = id;
        os.descricao = descricao;
        os.funcionarioId = funcionarioId;
        os.status = StatusOrdemServico.ABERTA;
        os.dataInicio = dataInicio;
        os.dataFim = dataFim;
        return os;
    }

    public static OrdemServico reconstituir(OrdemServicoId id, String descricao, FuncionarioId funcionarioId,
                                            StatusOrdemServico status, LocalDate dataInicio, LocalDate dataFim) {
        OrdemServico os = new OrdemServico();
        os.id = id;
        os.descricao = descricao;
        os.funcionarioId = funcionarioId;
        os.status = status;
        os.dataInicio = dataInicio;
        os.dataFim = dataFim;
        return os;
    }

    public void concluir() {
        this.status = StatusOrdemServico.CONCLUIDA;
    }

    public void cancelar() {
        this.status = StatusOrdemServico.CANCELADA;
    }

    public boolean isAtiva() {
        return this.status == StatusOrdemServico.ABERTA;
    }
}
