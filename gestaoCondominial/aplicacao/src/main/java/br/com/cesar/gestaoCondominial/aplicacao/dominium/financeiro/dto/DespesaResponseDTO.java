package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.StatusDespesa;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DespesaResponseDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private CategoriaDespesa categoria;
    private TipoDespesa tipo;
    private StatusDespesa status;

    public DespesaResponseDTO(Despesa despesa) {
        this.id = despesa.getId();
        this.descricao = despesa.getDescricao();
        this.valor = despesa.getValor();
        this.data = despesa.getData();
        this.categoria = despesa.getCategoria();
        this.tipo = despesa.getTipo();
        this.status = despesa.getStatus();
    }
}
