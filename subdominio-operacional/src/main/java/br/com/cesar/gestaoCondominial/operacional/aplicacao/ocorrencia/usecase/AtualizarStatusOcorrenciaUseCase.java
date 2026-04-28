package br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;

@Service
public class AtualizarStatusOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public AtualizarStatusOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia executar(Long ocorrenciaId, String novoStatusStr) {
        Ocorrencia ocorrencia = repository.buscarPorId(ocorrenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada com o ID: " + ocorrenciaId));

        Ocorrencia.StatusOcorrencia novoStatus;
        try {
            novoStatus = Ocorrencia.StatusOcorrencia.valueOf(novoStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + novoStatusStr);
        }

        ocorrencia.atualizarStatus(novoStatus);

        return repository.salvar(ocorrencia);
    }
}