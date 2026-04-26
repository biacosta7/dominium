package com.dominium.backend.application.unidade.usecase;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.unidade.dto.UnidadeResponseDTO;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import java.util.List;

@Service
public class GetUnidadeUseCase {
    private final UnidadeRepository unidadeRepository;

    public GetUnidadeUseCase(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public List<UnidadeResponseDTO> findAll() {
        return unidadeRepository.findAll().stream()
                .map(UnidadeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UnidadeResponseDTO findById(Long id) {

        Unidade unidade = unidadeRepository.findById(new UnidadeId(id))
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));
        return UnidadeResponseDTO.fromEntity(unidade);
    }
}