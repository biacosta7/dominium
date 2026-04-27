package com.dominium.backend.application.financeiro.usecase;

import org.springframework.stereotype.Service;

import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;

@Service
public class GetOrcamentoPorAnoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public GetOrcamentoPorAnoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public Orcamento execute(Integer ano) {
        return orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano: " + ano));
    }
}
