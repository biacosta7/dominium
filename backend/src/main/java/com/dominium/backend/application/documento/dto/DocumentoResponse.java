package com.dominium.backend.application.documento.dto;

import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.VersaoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentoResponse(
        String id,
        String nome,
        String categoria,
        String status,
        LocalDate dataValidade,
        LocalDateTime dataCriacao,
        Integer versaoAtual,
        String nomeArquivoAtual
) {
    public static DocumentoResponse from(Documento d, VersaoDocumento versaoAtual) {
        return new DocumentoResponse(
                d.getId().getValor(),
                d.getNome(),
                d.getCategoria().name(),
                d.getStatus().name(),
                d.getDataValidade(),
                d.getDataCriacao(),
                versaoAtual != null ? versaoAtual.getNumeroVersao() : null,
                versaoAtual != null ? versaoAtual.getNomeArquivo() : null
        );
    }
}
