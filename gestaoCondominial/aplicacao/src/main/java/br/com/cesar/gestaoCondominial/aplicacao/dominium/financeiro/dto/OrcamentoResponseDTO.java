package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Orcamento;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrcamentoResponseDTO {
    private Long id;
    private Integer ano;
    private BigDecimal valorTotal;
    private BigDecimal valorGasto;
    private BigDecimal saldoDisponivel;

    public OrcamentoResponseDTO(Orcamento orcamento) {
        this.id = orcamento.getId();
        this.ano = orcamento.getAno();
        this.valorTotal = orcamento.getValorTotal();
        this.valorGasto = orcamento.getValorGasto();
        this.saldoDisponivel = orcamento.getSaldoDisponivel();
    }
}
