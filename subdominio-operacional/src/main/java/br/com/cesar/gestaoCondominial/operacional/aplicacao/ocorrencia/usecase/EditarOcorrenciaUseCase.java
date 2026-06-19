package br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaRequestDTO;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoPenalidade;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import org.springframework.stereotype.Service;

@Service
public class EditarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public EditarOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia executar(Long id, OcorrenciaRequestDTO dto) {
        Ocorrencia ocorrencia = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada: " + id));

        ocorrencia.setDescricao(dto.getDescricao());

        if (dto.getUnidadeId() != null) {
            ocorrencia.setUnidadeId(new UnidadeId(dto.getUnidadeId()));
        }

        if (dto.getPenalidade() != null && !dto.getPenalidade().isEmpty()) {
            try {
                ocorrencia.setPenalidade(TipoPenalidade.valueOf(dto.getPenalidade().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return repository.atualizar(id, ocorrencia);
    }
}