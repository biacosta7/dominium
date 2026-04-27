package com.dominium.backend.application.documento.dto;

import com.dominium.backend.domain.documento.VersaoDocumento;

import java.time.LocalDateTime;

public record VersaoDocumentoResponse(
        Long id,
        int numeroVersao,
        String nomeArquivo,
        Long uploadadoPor,
        LocalDateTime uploadadoEm
) {
    public static VersaoDocumentoResponse from(VersaoDocumento v) {
        return new VersaoDocumentoResponse(
                v.getId(),
                v.getNumeroVersao(),
                v.getNomeArquivo(),
                v.getUploadadoPor(),
                v.getUploadadoEm()
        );
    }
}
