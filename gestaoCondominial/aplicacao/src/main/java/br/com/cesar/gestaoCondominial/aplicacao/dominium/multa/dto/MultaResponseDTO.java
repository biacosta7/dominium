package com.dominium.backend.application.multa.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.multa.TipoValorMulta;

import lombok.Data;

@Data
public class MultaResponseDTO {

    private Long id;
    private Long ocorrenciaId;
    private Long unidadeId;

    private String descricao;
    private BigDecimal valor;
    private BigDecimal valorBase;

    private TipoValorMulta tipoValor;
    private StatusMulta status;

    private Integer reincidencia;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataPagamento;

    private BigDecimal valorPago;

    private String justificativaContestacao;
    private LocalDateTime dataContestacao;

    public static MultaResponseDTO fromEntity(Multa multa) {
        MultaResponseDTO dto = new MultaResponseDTO();

        dto.setId(multa.getId().getValor());
        dto.setOcorrenciaId(multa.getOcorrenciaId());
        dto.setUnidadeId(multa.getUnidade().getId().getValor());

        dto.setDescricao(multa.getDescricao());
        dto.setValor(multa.getValor());
        dto.setValorBase(multa.getValorBase());

        dto.setTipoValor(multa.getTipoValor());
        dto.setStatus(multa.getStatus());

        dto.setReincidencia(multa.getReincidencia());

        dto.setDataCriacao(multa.getDataCriacao());
        dto.setDataPagamento(multa.getDataPagamento());

        dto.setValorPago(multa.getValorPago());
        // Adicionar ao MultaResponseDTO
        // Dentro do fromEntity()

        dto.setJustificativaContestacao(
            multa.getJustificativaContestacao()
        );

        dto.setDataContestacao(
            multa.getDataContestacao()
        );
        return dto;
    }
}