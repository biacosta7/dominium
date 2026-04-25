package com.dominium.backend.application.unidade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dominium.backend.domain.unidade.StatusAdimplencia;
import com.dominium.backend.domain.unidade.Unidade;

import lombok.Data;

@Data
public class UnidadeResponseDTO {

    private Long id;
    private String numero;
    private String bloco;

    private Long proprietarioId;
    private Long inquilinoId;

    private StatusAdimplencia status;
    private BigDecimal saldoDevedor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UnidadeResponseDTO fromEntity(Unidade unidade) {
        UnidadeResponseDTO dto = new UnidadeResponseDTO();

        dto.setId(unidade.getId());
        dto.setNumero(unidade.getNumero());
        dto.setBloco(unidade.getBloco());

        dto.setProprietarioId(
            unidade.getProprietario() != null ? unidade.getProprietario().getId() : null
        );

        dto.setInquilinoId(
            unidade.getInquilino() != null ? unidade.getInquilino().getId() : null
        );

        dto.setStatus(unidade.getStatus());
        dto.setSaldoDevedor(unidade.getSaldoDevedor());
        dto.setCreatedAt(unidade.getCreatedAt());
        dto.setUpdatedAt(unidade.getUpdatedAt());

        return dto;
    }
}