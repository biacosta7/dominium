package com.dominium.backend.application.unidade.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.unidade.dto.HistoricoTitularidadeResponseDTO;
import com.dominium.backend.domain.unidade.repository.HistoricoTitularidadeRepository;
import com.dominium.backend.domain.unidade.UnidadeId;

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