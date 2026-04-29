package br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;

import java.util.List;
import java.util.Optional;

public interface VersaoDocumentoRepository {
    void save(VersaoDocumento versao);
    Optional<VersaoDocumento> findUltimaVersao(DocumentoId documentoId);
    List<VersaoDocumento> findHistorico(DocumentoId documentoId);
}
