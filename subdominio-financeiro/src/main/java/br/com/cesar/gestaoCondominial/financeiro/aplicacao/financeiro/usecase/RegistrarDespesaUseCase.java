package br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Orcamento;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.OrcamentoRepository;
import br.com.cesar.gestaoCondominial.financeiro.aplicacao.financeiro.service.RateioService;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy.AprovacaoDespesaStrategy;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy.AprovacaoDespesaOrdinariaStrategy;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.strategy.AprovacaoDespesaExtraordinariaStrategy;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.DomainException;
import br.com.cesar.gestaoCondominial.dominio.dominium.exceptions.ResourceNotFoundException;

@Service
public class RegistrarDespesaUseCase {

    private final DespesaRepository despesaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final RateioService rateioService;
    private final Map<TipoDespesa, AprovacaoDespesaStrategy> strategies;

    public RegistrarDespesaUseCase(DespesaRepository despesaRepository, 
                                OrcamentoRepository orcamentoRepository,
                                RateioService rateioService) {
        this.despesaRepository = despesaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.rateioService = rateioService;
        this.strategies = new HashMap<>();
        this.strategies.put(TipoDespesa.ORDINARIA, new AprovacaoDespesaOrdinariaStrategy());
        this.strategies.put(TipoDespesa.EXTRAORDINARIA, new AprovacaoDespesaExtraordinariaStrategy());
    }

    @Transactional
    public Despesa execute(String descricao, BigDecimal valor, LocalDate data, CategoriaDespesa categoria, TipoDespesa tipo) {
        int ano = data.getYear();
        Orcamento orcamento = orcamentoRepository.findByAno(ano)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado para o ano " + ano));

        if (orcamento.getSaldoDisponivel().compareTo(valor) < 0) {
            throw new DomainException("Saldo insuficiente no orçamento para esta despesa. Saldo disponível: " + orcamento.getSaldoDisponivel());
        }

        Despesa despesa = new Despesa(descricao, valor, data, categoria, tipo, StatusDespesa.PENDENTE, orcamento.getId());
        
        // Aplicar Strategy para determinar status inicial
        AprovacaoDespesaStrategy strategy = strategies.get(tipo);
        StatusDespesa statusInicial = strategy.determinarStatusInicial(despesa);
        despesa.setStatus(statusInicial);

        // Se já nascer aprovada, atualiza o gasto do orçamento
        if (despesa.getStatus() == StatusDespesa.APROVADA) {
            orcamento.adicionarDespesa(valor);
            orcamentoRepository.save(orcamento);
            
            // Se for extraordinária, realiza o rateio
            if (tipo == TipoDespesa.EXTRAORDINARIA) {
                rateioService.realizarRateio(despesa);
            }
        }

        return despesaRepository.save(despesa);
    }
}
