package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.storage.ArmazenamentoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BaixarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final ArmazenamentoDocumento armazenamento;

    public BaixarDocumentoUseCase(DocumentoRepository documentoRepository,
                                   VersaoDocumentoRepository versaoRepository,
                                   ArmazenamentoDocumento armazenamento) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.armazenamento = armazenamento;
    }

    public record ResultadoDownload(String nomeArquivo, byte[] conteudo) {}

    public ResultadoDownload executar(String documentoId) {
        Documento documento = documentoRepository.findById(DocumentoId.de(documentoId))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        if (!documento.isAtivo()) {
            throw new DomainException("Documento inativo não pode ser baixado");
        }

        VersaoDocumento versao = versaoRepository.findUltimaVersao(documento.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma versão encontrada"));

        byte[] conteudo = armazenamento.carregar(versao.getCaminhoArquivo());
        return new ResultadoDownload(versao.getNomeArquivo(), conteudo);
    }
}
