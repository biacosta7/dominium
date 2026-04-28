package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.service.RateioService;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

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
