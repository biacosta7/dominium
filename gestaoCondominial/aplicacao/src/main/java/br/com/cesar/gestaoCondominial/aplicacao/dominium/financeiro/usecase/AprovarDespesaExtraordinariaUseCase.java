package com.dominium.backend.application.financeiro.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dominium.backend.domain.financeiro.Despesa;
import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.StatusDespesa;
import com.dominium.backend.domain.financeiro.TipoDespesa;
import com.dominium.backend.domain.financeiro.repository.DespesaRepository;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.financeiro.service.RateioService;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import com.dominium.backend.domain.shared.exceptions.ResourceNotFoundException;

@Service
public class AprovarDespesaExtraordinariaUseCase {

    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final RateioService rateioService;

    public AprovarDespesaExtraordinariaUseCase(DespesaRepository despesaRepository, 
                                            OrcamentoRepository orcamentoRepository,
                                            RateioService rateioService) {
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.rateioService = rateioService;
    }

    @Transactional
    public Despesa execute(Long despesaId) {
        Despesa despesa = despesaRepository.findById(despesaId)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada: " + despesaId));

        if (despesa.getStatus() != StatusDespesa.PENDENTE) {
            throw new DomainException("Apenas despesas pendentes podem ser aprovadas.");
        }

        Orcamento orcamento = orcamentoRepository.findById(despesa.getOrcamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento associado não encontrado."));

        if (orcamento.getSaldoDisponivel().compareTo(despesa.getValor()) < 0) {
            throw new DomainException("Saldo insuficiente no orçamento para aprovar esta despesa.");
        }

        despesa.setStatus(StatusDespesa.APROVADA);
        orcamento.adicionarDespesa(despesa.getValor());

        orcamentoRepository.save(orcamento);

        if (despesa.getTipo() == TipoDespesa.EXTRAORDINARIA) {
            rateioService.realizarRateio(despesa);
        }

        return despesaRepository.save(despesa);
    }
}
