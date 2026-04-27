package com.dominium.backend.application.morador.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.dominium.backend.application.morador.dto.VinculoResponseDTO;
import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;

import org.springframework.stereotype.Service;

@Service
public class GetVinculosPorUnidadeUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;

    public GetVinculosPorUnidadeUseCase(VinculoMoradorRepository vinculoMoradorRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
    }

    public List<VinculoResponseDTO> execute(Long unidadeId) {
        return vinculoMoradorRepository.findByUnidadeIdAndStatus(unidadeId, StatusVinculo.ATIVO)
                .stream()
                .map(VinculoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
