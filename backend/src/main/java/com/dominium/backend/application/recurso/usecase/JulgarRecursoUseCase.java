package com.dominium.backend.application.recurso.usecase;

import com.dominium.backend.application.recurso.dto.JulgarRecursoRequestDTO;
import com.dominium.backend.domain.recurso.Recurso;
import com.dominium.backend.domain.recurso.RecursoId;
import com.dominium.backend.domain.recurso.StatusRecurso;
import com.dominium.backend.domain.recurso.repository.RecursoRepository;
import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class JulgarRecursoUseCase {

    private final RecursoRepository recursoRepository;
    private final MultaRepository multaRepository;

    public void execute(UUID recursoId, JulgarRecursoRequestDTO dto) {
        Recurso recurso = recursoRepository.buscarPorId(new RecursoId(recursoId))
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado."));

        recurso.julgar(dto.getStatus(), dto.getJustificativa());
        recursoRepository.atualizar(recurso);

        Multa multa = multaRepository.findById(recurso.getMultaId())
                .orElseThrow(() -> new IllegalArgumentException("Multa não encontrada."));

        if (dto.getStatus() == StatusRecurso.DEFERIDO) {
            if (dto.isCancelarMulta()) {
                multa.setValor(BigDecimal.ZERO); // Zerando o valor para representar cancelamento
            } else if (dto.getNovoValorMulta() != null) {
                multa.setValor(dto.getNovoValorMulta());
            }
            multaRepository.save(multa);
        }
    }
}