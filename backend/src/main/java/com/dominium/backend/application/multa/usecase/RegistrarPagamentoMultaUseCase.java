package com.dominium.backend.application.multa.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dominium.backend.application.multa.dto.MultaResponseDTO;
import com.dominium.backend.application.multa.dto.RegistrarPagamentoRequestDTO;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.multa.repository.MultaRepository;

@Service
public class RegistrarPagamentoMultaUseCase {

    private final MultaRepository multaRepository;

    public RegistrarPagamentoMultaUseCase(
            MultaRepository multaRepository
    ) {
        this.multaRepository = multaRepository;
    }

    public MultaResponseDTO execute(
            Long multaId,
            RegistrarPagamentoRequestDTO request
    ) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Multa não encontrada."));

        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new IllegalStateException(
                    "Esta multa já foi paga."
            );
        }

        multa.setValorPago(request.getValorPago());
        multa.setDataPagamento(LocalDateTime.now());
        multa.setStatus(StatusMulta.PAGA);
        multa.setUpdatedAt(LocalDateTime.now());

        Multa salva = multaRepository.save(multa);

        return MultaResponseDTO.fromEntity(salva);
    }
}