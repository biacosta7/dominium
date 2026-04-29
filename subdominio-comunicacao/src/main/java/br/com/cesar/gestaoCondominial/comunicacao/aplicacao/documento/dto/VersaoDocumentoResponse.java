package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.dto;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;

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
                v.getVersao(),
                v.getNomeArquivo(),
                v.getEnviadoPor(),
                v.getDataUpload()
        );
    }
}
