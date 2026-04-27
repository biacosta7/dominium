package br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.TipoPenalidade;
import br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.ocorrencia.dto.OcorrenciaRequestDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class GerenciarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;

    public GerenciarOcorrenciaUseCase(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia executar(OcorrenciaRequestDTO dto) {
        Ocorrencia ocorrencia = new Ocorrencia();

        // Agora esses métodos existem na classe Ocorrencia
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