package com.dominium.backend.domain.documento;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class VersaoDocumento {

    private Long id;
    private DocumentoId documentoId;
    private int numeroVersao;
    private String nomeArquivo;
    private String caminhoArquivo;
    private Long uploadadoPor;
    private LocalDateTime uploadadoEm;

    public static VersaoDocumento criar(DocumentoId documentoId, int numeroVersao,
                                        String nomeArquivo, String caminhoArquivo, Long uploadadoPor) {
        VersaoDocumento v = new VersaoDocumento();
        v.documentoId = documentoId;
        v.numeroVersao = numeroVersao;
        v.nomeArquivo = nomeArquivo;
        v.caminhoArquivo = caminhoArquivo;
        v.uploadadoPor = uploadadoPor;
        v.uploadadoEm = LocalDateTime.now();
        return v;
    }
}
