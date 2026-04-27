package com.dominium.backend.application.morador.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dominium.backend.application.morador.dto.VinculoRequestDTO;
import com.dominium.backend.application.morador.dto.VinculoResponseDTO;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;

@Service
public class UpdateVinculoMoradorUseCase {

    private final VinculoMoradorRepository vinculoMoradorRepository;

    public UpdateVinculoMoradorUseCase(VinculoMoradorRepository vinculoMoradorRepository) {
        this.vinculoMoradorRepository = vinculoMoradorRepository;
    }

    @Transactional
    public VinculoResponseDTO execute(Long vinculoId, VinculoRequestDTO request) {
        VinculoMorador vinculo = vinculoMoradorRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado"));

        if (request.getTipo() != null) {
            vinculo.setTipo(request.getTipo());
        }

        VinculoMorador updated = vinculoMoradorRepository.save(vinculo);

        return VinculoResponseDTO.fromEntity(updated);
    }
}
