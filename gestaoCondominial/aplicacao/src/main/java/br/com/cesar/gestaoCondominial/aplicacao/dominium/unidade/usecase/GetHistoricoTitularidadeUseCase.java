package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto.HistoricoTitularidadeResponseDTO;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.HistoricoTitularidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;

@Service
public class GetHistoricoTitularidadeUseCase {

    private final HistoricoTitularidadeRepository historicoRepository;

    public GetHistoricoTitularidadeUseCase(HistoricoTitularidadeRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    // Alterado de UnidadeId para Long
    public List<HistoricoTitularidadeResponseDTO> execute(Long unidadeId) {
        // Criamos o Value Object aqui para passar ao repositório
        return historicoRepository.findByUnidadeId(new UnidadeId(unidadeId))
                .stream()
                .map(HistoricoTitularidadeResponseDTO::fromEntity)
                .toList();
    }
}