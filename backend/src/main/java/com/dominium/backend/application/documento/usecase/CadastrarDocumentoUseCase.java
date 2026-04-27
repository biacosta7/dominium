package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.application.documento.storage.ArmazenamentoDocumento;
import com.dominium.backend.domain.documento.*;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import com.dominium.backend.domain.documento.repository.VersaoDocumentoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.usuario.TipoUsuario;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CadastrarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final ArmazenamentoDocumento armazenamento;
    private final UsuarioRepository usuarioRepository;

    public CadastrarDocumentoUseCase(DocumentoRepository documentoRepository,
                                      VersaoDocumentoRepository versaoRepository,
                                      ArmazenamentoDocumento armazenamento,
                                      UsuarioRepository usuarioRepository) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.armazenamento = armazenamento;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Documento executar(Long sindicoId, String nome, CategoriaDocumento categoria,
                               LocalDate dataValidade, String nomeArquivo, byte[] conteudo) {
        Usuario sindico = usuarioRepository.findById(sindicoId)
                .orElseThrow(() -> new DomainException("Usuário não encontrado"));

        if (sindico.getTipo() != TipoUsuario.SINDICO) {
            throw new DomainException("Apenas o síndico pode cadastrar documentos");
        }

        Documento documento = Documento.criar(DocumentoId.novo(), nome, categoria, dataValidade, sindicoId);
        documentoRepository.save(documento);

        String caminho = armazenamento.salvar(documento.getId().getValor(), 1, nomeArquivo, conteudo);
        VersaoDocumento versao = VersaoDocumento.criar(documento.getId(), 1, nomeArquivo, caminho, sindicoId);
        versaoRepository.save(versao);

        return documento;
    }
}
