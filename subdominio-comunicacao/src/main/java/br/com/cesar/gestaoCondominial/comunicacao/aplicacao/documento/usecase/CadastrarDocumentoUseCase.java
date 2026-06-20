package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.SindicoService;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.*;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CadastrarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final ArmazenamentoDocumento armazenamento;
    private final SindicoService sindicoService;

    public CadastrarDocumentoUseCase(DocumentoRepository documentoRepository,
                                      VersaoDocumentoRepository versaoRepository,
                                      ArmazenamentoDocumento armazenamento,
                                      SindicoService sindicoService) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.armazenamento = armazenamento;
        this.sindicoService = sindicoService;
    }

    @Transactional
    public DocumentoComVersao executar(Long sindicoId, String nome, CategoriaDocumento categoria,
                                       LocalDate dataValidade, String nomeArquivo, byte[] conteudo) {
        sindicoService.validarPermissao(sindicoId);

        Documento documento = Documento.criar(DocumentoId.novo(), nome, categoria, dataValidade, sindicoId);
        documentoRepository.save(documento);

        String caminho = armazenamento.salvar(documento.getId().getValor(), 1, nomeArquivo, conteudo);
        VersaoDocumento versao = VersaoDocumento.criar(documento.getId(), 1, nomeArquivo, caminho, sindicoId);
        versaoRepository.save(versao);

        return new DocumentoComVersao(documento, versao);
    }
}
