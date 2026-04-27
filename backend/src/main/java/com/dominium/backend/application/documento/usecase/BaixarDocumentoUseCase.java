package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.application.documento.storage.ArmazenamentoDocumento;
import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.DocumentoId;
import com.dominium.backend.domain.documento.VersaoDocumento;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import com.dominium.backend.domain.documento.repository.VersaoDocumentoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;
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
