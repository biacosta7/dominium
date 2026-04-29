package br.com.cesar.gestaoCondominial.comunicacao.dominio.documento;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersaoDocumento {

    private Long id;
    private DocumentoId documentoId;
    private Integer versao;
    private String nomeArquivo;
    private String caminhoArquivo;
    private Long enviadoPor;
    private LocalDateTime dataUpload;

    public static VersaoDocumento criar(DocumentoId documentoId, Integer versao, String nomeArquivo,
                                         String caminhoArquivo, Long enviadoPor) {
        VersaoDocumento v = new VersaoDocumento();
        v.documentoId = documentoId;
        v.versao = versao;
        v.nomeArquivo = nomeArquivo;
        v.caminhoArquivo = caminhoArquivo;
        v.enviadoPor = enviadoPor;
        v.dataUpload = LocalDateTime.now();
        return v;
    }
}
