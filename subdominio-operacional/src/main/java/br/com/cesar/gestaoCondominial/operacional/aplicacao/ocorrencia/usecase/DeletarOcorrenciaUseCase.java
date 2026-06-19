package br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;

@Service
public class DeletarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public DeletarOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public void executar(Long id) {
        repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada: " + id));
        repository.deletar(id);
    }
}