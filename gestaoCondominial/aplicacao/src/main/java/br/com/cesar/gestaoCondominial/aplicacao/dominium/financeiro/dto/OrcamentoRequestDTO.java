package br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrcamentoRequestDTO {
    @NotNull(message = "O ano é obrigatório")
    private Integer ano;

    @NotNull(message = "O valor total é obrigatório")
    private BigDecimal valorTotal;
}
