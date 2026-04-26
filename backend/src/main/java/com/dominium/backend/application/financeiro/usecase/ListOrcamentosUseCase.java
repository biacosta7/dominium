package com.dominium.backend.application.financeiro.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;

@Service
public class ListOrcamentosUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public ListOrcamentosUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public List<Orcamento> execute() {
        return orcamentoRepository.findAll();
    }
}
