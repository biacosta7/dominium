package com.dominium.backend.application.taxa.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaxaRequestDTO {
    private Long unidadeId;
    private BigDecimal valorBase;
    private BigDecimal valorMultas;
    private LocalDate dataVencimento;
}