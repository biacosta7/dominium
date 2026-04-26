package com.dominium.backend.application.financeiro.usecase;

import org.springframework.stereotype.Service;

import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.repository.DespesaRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;

@Service
public class GetDespesaUseCase {

    private final DespesaRepository despesaRepository;

    public GetDespesaUseCase(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    public Despesa execute(Long id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada: " + id));
    }
}
