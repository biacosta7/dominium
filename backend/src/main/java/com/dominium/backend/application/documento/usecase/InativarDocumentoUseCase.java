package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.DocumentoId;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InativarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;

    public InativarDocumentoUseCase(DocumentoRepository documentoRepository, UsuarioRepository usuarioRepository) {
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Documento executar(Long sindicoId, String documentoId) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode inativar documentos");
        }

        Documento documento = documentoRepository.findById(DocumentoId.de(documentoId))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        documento.inativar();
        return documentoRepository.save(documento);
    }
}
