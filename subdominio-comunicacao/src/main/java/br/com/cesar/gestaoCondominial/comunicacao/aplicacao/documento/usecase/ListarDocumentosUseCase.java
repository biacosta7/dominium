package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarDocumentosUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;

    public ListarDocumentosUseCase(DocumentoRepository documentoRepository,
                                    VersaoDocumentoRepository versaoRepository) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
    }

    public List<DocumentoComVersao> executar(boolean incluirInativos) {
        var documentos = incluirInativos ? documentoRepository.findAll() : documentoRepository.findAtivos();
        return documentos.stream()
            .map(d -> new DocumentoComVersao(d, versaoRepository.findUltimaVersao(d.getId()).orElse(null)))
            .toList();
    }
}
