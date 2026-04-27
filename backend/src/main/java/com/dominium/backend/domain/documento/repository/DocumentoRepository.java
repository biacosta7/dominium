package com.dominium.backend.domain.documento.repository;

import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.DocumentoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocumentoRepository {
    Documento save(Documento documento);
    Optional<Documento> findById(DocumentoId id);
    List<Documento> findAtivos();
    List<Documento> findAll();
    List<Documento> findVencendoAte(LocalDate limite);
}
