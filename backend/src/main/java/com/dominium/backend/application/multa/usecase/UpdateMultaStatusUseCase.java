package com.dominium.backend.application.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.multa.dto.MultaResponseDTO;
import com.dominium.backend.application.multa.dto.UpdateMultaStatusRequestDTO;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.repository.MultaRepository;

@Service
public class UpdateMultaStatusUseCase {

    private final MultaRepository multaRepository;

    public UpdateMultaStatusUseCase(
            MultaRepository multaRepository
    ) {
        this.multaRepository = multaRepository;
    }

    public MultaResponseDTO execute(
            Long multaId,
            UpdateMultaStatusRequestDTO request
    ) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Multa não encontrada."));

        multa.setStatus(request.getStatus());
        multa.setUpdatedAt(LocalDateTime.now());

        Multa atualizada = multaRepository.save(multa);

        return MultaResponseDTO.fromEntity(atualizada);
    }
}