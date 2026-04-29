package br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository {
    void save(Documento documento);
    Optional<Documento> findById(DocumentoId id);
    List<Documento> findAll();
    List<Documento> findAtivos();
}
