package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.DocumentoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.VersaoDocumento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.VersaoDocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObterHistoricoDocumentoUseCase {

    private final VersaoDocumentoRepository versaoRepository;

    public ObterHistoricoDocumentoUseCase(VersaoDocumentoRepository versaoRepository) {
        this.versaoRepository = versaoRepository;
    }

    public List<VersaoDocumento> executar(String documentoId) {
        return versaoRepository.findHistorico(DocumentoId.de(documentoId));
    }
}
