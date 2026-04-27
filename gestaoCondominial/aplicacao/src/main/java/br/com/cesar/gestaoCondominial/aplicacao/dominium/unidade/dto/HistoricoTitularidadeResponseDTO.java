package com.dominium.backend.application.unidade.dto;

import java.time.LocalDateTime;

import com.dominium.backend.domain.unidade.HistoricoTitularidade;

import lombok.Data;

@Data
public class HistoricoTitularidadeResponseDTO {

    private Long id;
    private Long unidadeId;

    private Long proprietarioAnteriorId;
    private String proprietarioAnteriorNome;

    private Long novoProprietarioId;
    private String novoProprietarioNome;

    private LocalDateTime dataTransferencia;

    public static HistoricoTitularidadeResponseDTO fromEntity(HistoricoTitularidade historico) {
        HistoricoTitularidadeResponseDTO dto = new HistoricoTitularidadeResponseDTO();
    
        dto.setId(historico.getId());
        // Extraindo valor do UnidadeId
        dto.setUnidadeId(historico.getUnidadeId().getValor());
    
        // Se o ID do usuário for VO, adicione .getValor()
        dto.setProprietarioAnteriorId(historico.getProprietarioAnterior().getId());
        dto.setProprietarioAnteriorNome(historico.getProprietarioAnterior().getNome());
    
        dto.setNovoProprietarioId(historico.getNovoProprietario().getId());
        dto.setNovoProprietarioNome(historico.getNovoProprietario().getNome());
    
        dto.setDataTransferencia(historico.getDataTransferencia());
    
        return dto;
    }
}