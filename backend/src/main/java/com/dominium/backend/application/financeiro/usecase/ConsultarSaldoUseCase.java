package com.dominium.backend.application.financeiro.usecase;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;

@Service
public class ConsultarSaldoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public ConsultarSaldoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    public BigDecimal execute(Integer ano) {
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano " + ano));
        
        return orcamento.getSaldoDisponivel();
    }
}
