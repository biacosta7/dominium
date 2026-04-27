package com.dominium.backend.domain.documento.repository;

import com.dominium.backend.domain.documento.DocumentoId;
import com.dominium.backend.domain.documento.VersaoDocumento;

import java.util.List;
import java.util.Optional;

public interface VersaoDocumentoRepository {
    VersaoDocumento save(VersaoDocumento versao);
    List<VersaoDocumento> findByDocumentoId(DocumentoId documentoId);
    Optional<VersaoDocumento> findUltimaVersao(DocumentoId documentoId);
    int contarVersoes(DocumentoId documentoId);
}
