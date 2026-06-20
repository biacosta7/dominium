package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.SindicoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InativarDocumentoUseCase {

    private final DocumentoRepository documentoRepository;
    private final VersaoDocumentoRepository versaoRepository;
    private final SindicoService sindicoService;

    public InativarDocumentoUseCase(DocumentoRepository documentoRepository,
                                     VersaoDocumentoRepository versaoRepository,
                                     SindicoService sindicoService) {
        this.documentoRepository = documentoRepository;
        this.versaoRepository = versaoRepository;
        this.sindicoService = sindicoService;
    }

    @Transactional
    public DocumentoComVersao executar(Long sindicoId, String documentoId) {
        sindicoService.validarPermissao(sindicoId);

        Documento documento = documentoRepository.findById(DocumentoId.de(documentoId))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado"));

        documento.inativar();
        documentoRepository.save(documento);

        Optional<VersaoDocumento> ultimaVersao = versaoRepository.findUltimaVersao(documento.getId());
        return new DocumentoComVersao(documento, ultimaVersao.orElse(null));
    }
}
