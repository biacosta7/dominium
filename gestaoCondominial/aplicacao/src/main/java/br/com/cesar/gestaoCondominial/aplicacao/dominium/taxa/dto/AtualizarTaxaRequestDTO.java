package com.dominium.backend.application.taxa.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AtualizarTaxaRequestDTO {
    private BigDecimal novoValorBase;
    private BigDecimal novasMultas;
}