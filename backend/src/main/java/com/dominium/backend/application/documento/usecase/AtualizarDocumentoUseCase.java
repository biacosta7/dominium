package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.application.documento.storage.ArmazenamentoDocumento;
import com.dominium.backend.domain.documento.*;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import com.dominium.backend.domain.documento.repository.VersaoDocumentoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final ArmazenamentoDocumento armazenamento;
    private final UsuarioRepository usuarioRepository;

    public AtualizarDocumentoUseCase(DocumentoRepository documentoRepository,
                                      VersaoDocumentoRepository versaoRepository,
                                      ArmazenamentoDocumento armazenamento,
                                      UsuarioRepository usuarioRepository) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.armazenamento = armazenamento;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public VersaoDocumento executar(Long sindicoId, String documentoId, String nomeArquivo, byte[] conteudo) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode atualizar documentos");
        }

        Documento documento = documentoRepository.findById(DocumentoId.de(documentoId))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        if (!documento.isAtivo()) {
            throw new DomainException("Não é possível atualizar um documento inativo");
        }

        int proximaVersao = versaoRepository.contarVersoes(documento.getId()) + 1;
        String caminho = armazenamento.salvar(documentoId, proximaVersao, nomeArquivo, conteudo);
        VersaoDocumento novaVersao = VersaoDocumento.criar(documento.getId(), proximaVersao, nomeArquivo, caminho, sindicoId);
        return versaoRepository.save(novaVersao);
    }
}
