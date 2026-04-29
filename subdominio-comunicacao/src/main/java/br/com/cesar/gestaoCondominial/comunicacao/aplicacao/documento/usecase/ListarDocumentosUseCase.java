package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarDocumentosUseCase {

    private final DocumentoRepository documentoRepository;

    public ListarDocumentosUseCase(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public List<Documento> executar(boolean incluirInativos) {
        return incluirInativos ? documentoRepository.findAll() : documentoRepository.findAtivos();
    }
}
