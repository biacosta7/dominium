package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarDocumentosUseCase {

    private final DocumentoRepository documentoRepository;

    public ListarDocumentosUseCase(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public List<Documento> executar(boolean incluirInativos) {
        return incluirInativos ? documentoRepository.findAll() : documentoRepository.findAtivos();
    }
}
