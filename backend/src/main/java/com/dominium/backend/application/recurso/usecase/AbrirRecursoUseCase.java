package com.dominium.backend.application.recurso.usecase;

import com.dominium.backend.application.recurso.dto.AbrirRecursoRequestDTO;
import com.dominium.backend.domain.recurso.Recurso;
import com.dominium.backend.domain.recurso.repository.RecursoRepository;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbrirRecursoUseCase {

    private final RecursoRepository recursoRepository;
    private final MultaRepository multaRepository;

    public UUID execute(AbrirRecursoRequestDTO dto) {
        Multa multa = multaRepository.findById(dto.getMultaId())
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));

        if (multa.getDataCriacao() != null && multa.getDataCriacao().plusDays(15).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Prazo máximo de 15 dias para recurso expirado.");
        }

        multa.setDataContestacao(LocalDateTime.now());
        multa.setJustificativaContestacao(dto.getMotivo());
        multaRepository.save(multa);

        Recurso recurso = Recurso.abrir(dto.getMultaId(), dto.getMoradorId(), dto.getMotivo());
        recursoRepository.salvar(recurso);

        return recurso.getId().getValue();
    }
}