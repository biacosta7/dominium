package com.dominium.backend.application.taxa.dto;

import com.dominium.backend.domain.taxa.TaxaCondominial;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaxaResponseDTO {
    private Long id;
    private Long unidadeId;
    private BigDecimal valorBase;
    private BigDecimal valorMultas;
    private BigDecimal valorTotal;
    private LocalDate dataVencimento;
    private LocalDateTime dataPagamento;
    private String status;

    public static TaxaResponseDTO fromDomain(TaxaCondominial taxa) {
        TaxaResponseDTO dto = new TaxaResponseDTO();

        if (taxa.getId() != null) {
            dto.setId(taxa.getId().getValor());
        }

        dto.setUnidadeId(taxa.getUnidadeId().getValor());
        dto.setValorBase(taxa.getValorBase());
        dto.setValorMultas(taxa.getValorMultas());
        dto.setValorTotal(taxa.getValorTotal());
        dto.setDataVencimento(taxa.getDataVencimento());
        dto.setDataPagamento(taxa.getDataPagamento());
        dto.setStatus(taxa.getStatus().name());
        return dto;
    }
}