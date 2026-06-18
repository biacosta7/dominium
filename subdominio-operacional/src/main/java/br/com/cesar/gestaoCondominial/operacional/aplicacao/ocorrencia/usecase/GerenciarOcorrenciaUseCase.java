package br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.usecase;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoPenalidade;
import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository.OcorrenciaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.Unidade;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.operacional.aplicacao.ocorrencia.dto.OcorrenciaRequestDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GerenciarOcorrenciaUseCase {

    private final OcorrenciaRepository repository;
    private final UnidadeRepository unidadeRepository;

    public GerenciarOcorrenciaUseCase(OcorrenciaRepository repository, UnidadeRepository unidadeRepository) {
        this.repository = repository;
        this.unidadeRepository = unidadeRepository;
    }

    public Ocorrencia executar(OcorrenciaRequestDTO dto) {
        if (dto.getUnidadeId() == null) {
            throw new RuntimeException("Ocorrência deve estar vinculada a uma unidade");
        }
        
        // Lookup unit by its number first (e.g. "108") to get its database primary key ID.
        Optional<Unidade> unidadeOpt = unidadeRepository.findAll().stream()
                .filter(u -> u.getNumero().equals(String.valueOf(dto.getUnidadeId())))
                .findFirst();
                
        Long actualUnidadeId = unidadeOpt.map(u -> u.getId().getValor()).orElse(dto.getUnidadeId());
        
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTipo(dto.getTipo());
        ocorrencia.setDescricao(dto.getDescricao());
        ocorrencia.setUnidadeId(new UnidadeId(actualUnidadeId));
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