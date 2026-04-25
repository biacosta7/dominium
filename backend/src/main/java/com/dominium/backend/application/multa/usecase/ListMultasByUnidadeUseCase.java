package com.dominium.backend.application.multa.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.multa.dto.MultaResponseDTO;
import com.dominium.backend.domain.multa.repository.MultaRepository;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;

@Service
public class ListMultasByUnidadeUseCase {

    private final MultaRepository multaRepository;
    private final UnidadeRepository unidadeRepository;

    public ListMultasByUnidadeUseCase(
            MultaRepository multaRepository,
            UnidadeRepository unidadeRepository
    ) {
        this.multaRepository = multaRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<MultaResponseDTO> execute(Long unidadeId) {

        unidadeRepository.findById(unidadeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Unidade não encontrada."));

        return multaRepository.findByUnidadeId(unidadeId)
                .stream()
                .map(MultaResponseDTO::fromEntity)
                .toList();
    }
}