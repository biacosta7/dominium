package com.dominium.backend.application.financeiro.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dominium.backend.domain.financeiro.CategoriaDespesa;
import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.DespesaRepository;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;

@Service
public class ListDespesasPorOrcamentoUseCase {

    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;

    public ListDespesasPorOrcamentoUseCase(DespesaRepository despesaRepository, OrcamentoRepository orcamentoRepository) {
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public List<Despesa> execute(Integer ano, CategoriaDespesa categoria) {
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano: " + ano));

        List<Despesa> despesas = despesaRepository.findByOrcamentoId(orcamento.getId());

        if (categoria != null) {
            return despesas.stream()
                    .filter(d -> d.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }

        return despesas;
    }
}
