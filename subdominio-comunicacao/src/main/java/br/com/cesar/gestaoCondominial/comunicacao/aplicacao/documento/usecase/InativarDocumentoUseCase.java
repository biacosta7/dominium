package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
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
        documentoRepository.save(documento);
        return documento;
    }
}
