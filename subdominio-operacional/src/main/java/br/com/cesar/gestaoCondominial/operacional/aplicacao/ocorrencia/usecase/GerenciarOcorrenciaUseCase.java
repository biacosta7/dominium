package br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoPenalidade;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaRequestDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GerenciarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public GerenciarOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia executar(OcorrenciaRequestDTO dto) {
        if (dto.getUnidadeId() == null) {
            throw new RuntimeException("Ocorrência deve estar vinculada a uma unidade");
        }
        
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setDescricao(dto.getDescricao());
        ocorrencia.setUnidadeId(new UnidadeId(dto.getUnidadeId()));
        ocorrencia.setDataRegistro(LocalDateTime.now());
        ocorrencia.setStatus(Ocorrencia.StatusOcorrencia.ABERTA);

        TipoPenalidade penalidade = TipoPenalidade.NENHUMA;
        if (dto.getPenalidade() != null && !dto.getPenalidade().isEmpty()) {
            try {
                penalidade = TipoPenalidade.valueOf(dto.getPenalidade().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore or handle invalid penalidade string
            }
        }
        ocorrencia.setPenalidade(penalidade);

        return repository.salvar(ocorrencia);
    }
}