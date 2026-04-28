package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.dominio.dominium.financeiro.TipoDespesa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DespesaRequestDTO {
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    private BigDecimal valor;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotNull(message = "A categoria é obrigatória")
    private CategoriaDespesa categoria;

    @NotNull(message = "O tipo é obrigatório")
    private TipoDespesa tipo;
}
