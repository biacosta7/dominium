package br.com.cesar.gestaoCondominial.aplicacao.dominium.taxa.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AtualizarTaxaRequestDTO {
    private BigDecimal novoValorBase;
    private BigDecimal novasMultas;
}