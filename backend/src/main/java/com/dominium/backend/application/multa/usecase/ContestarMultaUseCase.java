package com.dominium.backend.application.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.multa.dto.ContestarMultaRequestDTO;
import com.dominium.backend.application.multa.dto.MultaResponseDTO;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.MultaId; // Importado
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.multa.repository.MultaRepository;

@Service
public class ContestarMultaUseCase {

    private final MultaRepository multaRepository;

    public ContestarMultaUseCase(
            MultaRepository multaRepository
    ) {
        this.multaRepository = multaRepository;
    }

    public MultaResponseDTO execute(
            Long multaId,
            ContestarMultaRequestDTO request
    ) {
        // Conversão de Long para MultaId na busca
        Multa multa = multaRepository.findById(new MultaId(multaId))
                .orElseThrow(() ->
                        new IllegalArgumentException("Multa não encontrada."));

        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new IllegalStateException(
                    "Não é possível contestar uma multa já paga."
            );
        }

        if (multa.getStatus() == StatusMulta.CANCELADA) {
            throw new IllegalStateException(
                    "Não é possível contestar uma multa cancelada."
            );
        }

        multa.setStatus(StatusMulta.CONTESTADA);
        multa.setJustificativaContestacao(request.getJustificativa());
        multa.setDataContestacao(LocalDateTime.now());
        multa.setUpdatedAt(LocalDateTime.now());

        Multa salva = multaRepository.save(multa);

        return MultaResponseDTO.fromEntity(salva);
    }
}