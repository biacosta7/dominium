package com.dominium.backend.application.financeiro.usecase;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import com.dominium.backend.domain.shared.exceptions.DomainException;

@Service
public class CadastrarOrcamentoUseCase {

    private final OrcamentoRepository orcamentoRepository;

    public CadastrarOrcamentoUseCase(OrcamentoRepository orcamentoRepository) {
        this.orcamentoRepository = orcamentoRepository;
    }

    @Transactional
    public Orcamento execute(Integer ano, BigDecimal valorTotal) {
        if (orcamentoRepository.findByAno(ano).isPresent()) {
            throw new DomainException("Já existe um orçamento cadastrado para o ano " + ano);
        }

        Orcamento orcamento = new Orcamento(ano, valorTotal);
        return orcamentoRepository.save(orcamento);
    }
}
