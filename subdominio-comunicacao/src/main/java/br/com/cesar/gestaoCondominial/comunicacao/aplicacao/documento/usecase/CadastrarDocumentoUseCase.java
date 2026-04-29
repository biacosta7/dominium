package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.*;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.TipoUsuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.Usuario;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.repository.UsuarioRepository;
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
